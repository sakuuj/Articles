package by.sakuuj.articles.article.orchestration;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;

import java.util.UUID;

public interface OrchestratedArticleService {

    ArticleResponse create(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    ArticleResponse updateById(ArticleRequest articleRequest, UUID id, short version);

    void deleteById(UUID id);

}
