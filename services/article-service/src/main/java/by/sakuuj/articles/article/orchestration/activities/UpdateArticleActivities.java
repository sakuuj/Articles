package by.sakuuj.articles.article.orchestration.activities;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.UUID;

@ActivityInterface
public interface UpdateArticleActivities {

    String SEND_UPDATE_DOCUMENT_EVENT_ACTIVITY_NAME = "SendUpdateDocumentEvent";

    ArticleResponse updateByIdInDatabase(ArticleRequest articleRequest, UUID id, short version);

    @ActivityMethod(name = SEND_UPDATE_DOCUMENT_EVENT_ACTIVITY_NAME)
    void sendUpdateDocumentEvent(ArticleResponse articleResponse);
}
