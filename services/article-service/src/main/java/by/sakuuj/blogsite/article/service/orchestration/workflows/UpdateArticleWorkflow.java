package by.sakuuj.blogsite.article.service.orchestration.workflows;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import io.temporal.workflow.WorkflowInterface;

import java.util.UUID;

@WorkflowInterface
public interface UpdateArticleWorkflow {

    ArticleResponse updateArticle(ArticleRequest articleRequest, UUID id, short version);
}
