package by.sakuuj.blogsite.article.service.orchestration.workflows;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import io.temporal.workflow.WorkflowInterface;

import java.util.UUID;

@WorkflowInterface
public interface UpdateArticleWorkflow {

    ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version);
}
