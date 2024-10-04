package by.sakuuj.blogsite.article.service.orchestration;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.exception.IdempotencyTokenExistsException;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Duration;
import java.util.Map;

public class CreateArticleWorkflowImpl implements CreateArticleWorkflow {

    private static final Logger LOGGER = Workflow.getLogger(CreateArticleWorkflowImpl.class);

    private final RetryOptions retryOptions = RetryOptions.newBuilder()
            .setMaximumAttempts(5)
            .setMaximumInterval(Duration.ofSeconds(4))
            .setInitialInterval(Duration.ofSeconds(2))
            .setDoNotRetry(
                    DataIntegrityViolationException.class.getTypeName(),
                    IdempotencyTokenExistsException.class.getTypeName()
            )
            .build();

    private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setRetryOptions(retryOptions)
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .build();


    private final CreateArticleActivities activities = Workflow.newActivityStub(CreateArticleActivities.class, activityOptions,
            Map.of(
                    "SaveInElasticsearch", ActivityOptions.newBuilder()
                            .setRetryOptions(
                                    RetryOptions.newBuilder()
                                            .setInitialInterval(Duration.ofSeconds(2))
                                            .setMaximumInterval(Duration.ofHours(1))
                                            .setMaximumAttempts(Integer.MAX_VALUE)
                                            .setDoNotRetry(DataIntegrityViolationException.class.getTypeName())
                                            .build()
                            )
                            .setStartToCloseTimeout(Duration.ofSeconds(5))
                            .build()
            ));

    @Override
    public ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId) {

        LOGGER.debug("[BEFORE SAVING TO DATABASE]");
        ArticleResponse articleResponse = activities.saveInDatabase(articleRequest, idempotencyTokenId);
        LOGGER.debug("[SAVED TO DATABASE]");

        LOGGER.debug("[BEFORE SAVING TO ELASTICSEARCH]");
        activities.sendElasticsearchSaveEvent(articleResponse.id());
        LOGGER.debug("[SAVED TO ELASTICSEARCH]");

        return articleResponse;
    }
}
