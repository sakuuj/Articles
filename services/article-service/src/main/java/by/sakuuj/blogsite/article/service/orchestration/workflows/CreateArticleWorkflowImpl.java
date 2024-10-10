package by.sakuuj.blogsite.article.service.orchestration.workflows;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.service.orchestration.activities.CreateArticleActivities;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Map;

public class CreateArticleWorkflowImpl implements CreateArticleWorkflow {

    private static final Logger LOGGER = Workflow.getLogger(CreateArticleWorkflowImpl.class);
    private static final int KAFKA_PRODUCER_DELIVERY_TIMEOUT_MS_DEFAULT_IN_SEC = 120;


    private final RetryOptions retryOptionsZeroRetries = RetryOptions.newBuilder()
            .setMaximumAttempts(1)
            .build();

    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
            .setRetryOptions(retryOptionsZeroRetries)
            .setScheduleToCloseTimeout(Duration.ofSeconds(5))
            .build();


    private final CreateArticleActivities activities = Workflow.newActivityStub(CreateArticleActivities.class, defaultActivityOptions,
            Map.of(
                    CreateArticleActivities.SEND_SAVE_DOCUMENT_EVENT_ACTIVITY_NAME,
                    ActivityOptions.newBuilder()
                            .setRetryOptions(
                                    RetryOptions.newBuilder()
                                            .setInitialInterval(Duration.ofMinutes(5))
                                            .setMaximumInterval(Duration.ofHours(1))
                                            .setMaximumAttempts(Integer.MAX_VALUE)
                                            .build()
                            )
                            .setStartToCloseTimeout(Duration.ofSeconds(KAFKA_PRODUCER_DELIVERY_TIMEOUT_MS_DEFAULT_IN_SEC + 5))
                            .build()
            ));

    @Override
    public ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId) {

        LOGGER.debug("[BEFORE SAVING TO DATABASE]");
        ArticleResponse articleResponse = activities.saveInDatabase(articleRequest, idempotencyTokenId);
        LOGGER.debug("[SAVED TO DATABASE]");

        LOGGER.debug("[BEFORE SENDING SAVE EVENT]");
        activities.sendSaveDocumentEvent(articleResponse);
        LOGGER.debug("[SAVE EVENT HAS BEEN SENT]");

        return articleResponse;
    }
}
