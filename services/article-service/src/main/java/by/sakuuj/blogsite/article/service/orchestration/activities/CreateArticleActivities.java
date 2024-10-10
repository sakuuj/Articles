package by.sakuuj.blogsite.article.service.orchestration.activities;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CreateArticleActivities {

    String SEND_SAVE_DOCUMENT_EVENT_ACTIVITY_NAME = "SendSaveDocumentEvent";

    ArticleResponse saveInDatabase(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    @ActivityMethod(name = SEND_SAVE_DOCUMENT_EVENT_ACTIVITY_NAME)
    void sendSaveDocumentEvent(ArticleResponse articleResponse);
}
