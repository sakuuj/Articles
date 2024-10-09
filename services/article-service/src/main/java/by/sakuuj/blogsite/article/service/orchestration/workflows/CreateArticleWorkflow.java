package by.sakuuj.blogsite.article.service.orchestration.workflows;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateArticleWorkflow {

    @WorkflowMethod
    ArticleResponse createArticle(ArticleRequest articleRequest, IdempotencyTokenId idempotencyTokenId);
}
