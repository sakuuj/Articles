package by.sakuuj.blogsite.article.orchestration.workflows;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface UpdateArticleWorkflow {

    @WorkflowMethod
    ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version);
}
