package by.sakuuj.articles.article.orchestration.activities;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CreateArticleActivities {

    String SEND_SAVE_DOCUMENT_EVENT_ACTIVITY_NAME = "SendSaveDocumentEvent";

    ArticleResponse saveInDatabase(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    @ActivityMethod(name = SEND_SAVE_DOCUMENT_EVENT_ACTIVITY_NAME)
    void sendSaveDocumentEvent(ArticleResponse articleResponse);
}
