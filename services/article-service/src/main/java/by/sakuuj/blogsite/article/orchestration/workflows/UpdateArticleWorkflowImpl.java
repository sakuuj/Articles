package by.sakuuj.blogsite.article.orchestration.workflows;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.orchestration.activities.UpdateArticleActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class UpdateArticleWorkflowImpl implements UpdateArticleWorkflow {

    private static final Logger LOGGER = Workflow.getLogger(UpdateArticleWorkflowImpl.class);
    private static final int KAFKA_PRODUCER_DELIVERY_TIMEOUT_MS_DEFAULT_IN_SEC = 120;


    private final RetryOptions retryOptionsZeroRetries = RetryOptions.newBuilder()
            .setMaximumAttempts(1)
            .build();

    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
            .setRetryOptions(retryOptionsZeroRetries)
            .setScheduleToCloseTimeout(Duration.ofSeconds(5))
            .build();


    private final UpdateArticleActivities activities = Workflow.newActivityStub(UpdateArticleActivities.class, defaultActivityOptions,
            Map.of(
                    UpdateArticleActivities.SEND_UPDATE_DOCUMENT_EVENT_ACTIVITY_NAME,
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
    public ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version) {

        LOGGER.debug("[BEFORE UPDATE IN DATABASE]");
        ArticleResponse articleResponse = activities.updateByIdInDatabase(articleRequest, id, version);
        LOGGER.debug("[UPDATED IN DATABASE]");

        LOGGER.debug("[BEFORE SENDING UPDATE EVENT]");
        activities.sendUpdateDocumentEvent(articleResponse);
        LOGGER.debug("[UPDATE EVENT HAS BEEN SENT]");

        return articleResponse;
    }
}
