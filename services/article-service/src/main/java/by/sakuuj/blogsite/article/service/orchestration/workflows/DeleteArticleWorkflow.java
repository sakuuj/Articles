package by.sakuuj.blogsite.article.service.orchestration.workflows;

import io.temporal.workflow.WorkflowInterface;

import java.util.UUID;

@WorkflowInterface
public interface DeleteArticleWorkflow {

    void deleteDocumentById(UUID id);
}
