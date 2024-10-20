package by.sakuuj.articles.article.orchestration.workflows;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateArticleWorkflow {

    @WorkflowMethod
    ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);
}
