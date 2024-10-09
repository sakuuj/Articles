package by.sakuuj.blogsite.article.service.orchestration.workflows;

import by.sakuuj.blogsite.article.service.orchestration.activities.DeleteArticleActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class DeleteArticleWorkflowImpl implements DeleteArticleWorkflow {

    private static final Logger LOGGER = Workflow.getLogger(DeleteArticleWorkflowImpl.class);
    private static final int KAFKA_PRODUCER_DELIVERY_TIMEOUT_MS_DEFAULT_IN_SEC = 120;

    private final RetryOptions retryOptionsZeroRetries = RetryOptions.newBuilder()
            .setMaximumAttempts(1)
            .build();

    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
            .setRetryOptions(retryOptionsZeroRetries)
            .setScheduleToCloseTimeout(Duration.ofSeconds(5))
            .build();

    private final DeleteArticleActivities activities = Workflow.newActivityStub(DeleteArticleActivities.class, defaultActivityOptions,
            Map.of(
                    DeleteArticleActivities.SEND_DELETE_DOCUMENT_EVENT_ACTIVITY_NAME,
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
    public void deleteDocumentById(UUID id) {

        LOGGER.debug("[BEFORE DELETE IN DATABASE]");
        activities.deleteFromDatabase(id);
        LOGGER.debug("[DELETED FROM DATABASE]");

        LOGGER.debug("[BEFORE SENDING DELETE EVENT]");
        activities.deleteFromDatabase(id);
        LOGGER.debug("[DELETE EVENT HAS BEEN SENT]");
    }
}
