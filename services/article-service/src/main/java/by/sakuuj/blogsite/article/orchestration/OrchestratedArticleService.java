package by.sakuuj.blogsite.article.orchestration;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;

import java.util.UUID;

public interface OrchestratedArticleService {

    ArticleResponse create(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);

    ArticleResponse updateById(ArticleRequest articleRequest, UUID id, short version);

    void deleteById(UUID id);

}
