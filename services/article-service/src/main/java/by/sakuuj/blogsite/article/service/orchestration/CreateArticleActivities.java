package by.sakuuj.blogsite.article.service.orchestration;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.activity.ActivityInterface;

import java.util.UUID;

@ActivityInterface
public interface CreateArticleActivities {

    ArticleResponse saveInDatabase(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    void sendElasticsearchSaveEvent(UUID articleId);
}
