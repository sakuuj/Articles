package by.sakuuj.blogsite.article.service.orchestration.workflows;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateArticleWorkflow {

    @WorkflowMethod
    ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);
}
