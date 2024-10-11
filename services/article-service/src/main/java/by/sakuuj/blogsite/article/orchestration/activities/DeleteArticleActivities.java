package by.sakuuj.blogsite.article.orchestration.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.UUID;

@ActivityInterface
public interface DeleteArticleActivities {

    String SEND_DELETE_DOCUMENT_EVENT_ACTIVITY_NAME = "SendDeleteDocumentEvent";

    void deleteFromDatabase(UUID id);

    @ActivityMethod(name = SEND_DELETE_DOCUMENT_EVENT_ACTIVITY_NAME)
    void sendDeleteDocumentEvent(UUID id);
}
