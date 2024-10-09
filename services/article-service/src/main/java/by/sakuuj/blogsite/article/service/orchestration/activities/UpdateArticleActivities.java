package by.sakuuj.blogsite.article.service.orchestration.activities;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import io.temporal.activity.ActivityMethod;

import java.util.UUID;

public interface UpdateArticleActivities {

    String SEND_UPDATE_DOCUMENT_EVENT_ACTIVITY_NAME = "SendUpdateDocumentEvent";

    ArticleResponse updateByIdInDatabase(ArticleRequest articleRequest, UUID id, short version);

    @ActivityMethod(name = SEND_UPDATE_DOCUMENT_EVENT_ACTIVITY_NAME)
    void sendUpdateDocumentEvent(ArticleResponse articleResponse);
}
