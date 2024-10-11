package by.sakuuj.blogsite.article.orchestration;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;

import java.util.UUID;

public interface OrchestratedArticleService {

    ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version);

    void deleteDocumentById(UUID id);

}
