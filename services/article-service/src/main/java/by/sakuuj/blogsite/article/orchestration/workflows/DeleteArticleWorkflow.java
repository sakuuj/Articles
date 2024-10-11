package by.sakuuj.blogsite.article.orchestration.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface DeleteArticleWorkflow {

    @WorkflowMethod
    void deleteDocumentById(UUID id);
}
