package by.sakuuj.blogsite.article.service.orchestration;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;

import java.util.UUID;

public interface OrchestratedArticleService {

    ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version);

    void deleteDocumentById(UUID id);

}
