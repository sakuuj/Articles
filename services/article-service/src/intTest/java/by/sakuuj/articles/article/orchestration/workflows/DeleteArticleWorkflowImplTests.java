package by.sakuuj.articles.article.orchestration.workflows;

import by.sakuuj.articles.article.ArticleTestDataBuilder;
import by.sakuuj.articles.article.orchestration.activities.DeleteArticleActivities;
import by.sakuuj.articles.article.orchestration.workflows.DeleteArticleWorkflow;
import by.sakuuj.articles.article.orchestration.workflows.DeleteArticleWorkflowImpl;
import io.temporal.client.WorkflowFailedException;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.withSettings;

class DeleteArticleWorkflowImplTests {

    @RegisterExtension
    private static final TestWorkflowExtension testWorkflowExtension = TestWorkflowExtension.newBuilder()
            .registerWorkflowImplementationTypes(DeleteArticleWorkflowImpl.class)
            .setDoNotStart(true)
            .build();

    @AfterEach
    void closeWorkflowEnv(TestWorkflowEnvironment workflowEnv) {
        workflowEnv.shutdown();
    }

    @Test
    void shouldDeleteFromDB_AndThenSendDocumentEvent_WhenNoErrors(
            TestWorkflowEnvironment workflowEnv,
            DeleteArticleWorkflow workflow,
            Worker worker
    ) {
        DeleteArticleActivities activities = Mockito.mock(DeleteArticleActivities.class, withSettings().withoutAnnotations());

        ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
        UUID id = articleBuilder.getId();

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        workflow.deleteDocumentById(id);

        InOrder inOrder = Mockito.inOrder(activities);
        inOrder.verify(activities).deleteFromDatabase(id);
        inOrder.verify(activities).sendDeleteDocumentEvent(id);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotProceed_WhenCanNotDeleteFromDB(
            TestWorkflowEnvironment workflowEnv,
            DeleteArticleWorkflow workflow,
            Worker worker
    ) {
        DeleteArticleActivities activities = Mockito.mock(DeleteArticleActivities.class, withSettings().withoutAnnotations());

        ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
        UUID id = articleBuilder.getId();

        doThrow(new RuntimeException("ERROR")).when(activities).deleteFromDatabase(any());

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        assertThatThrownBy(() -> workflow.deleteDocumentById(id)).isInstanceOf(WorkflowFailedException.class);

        verify(activities, times(1)).deleteFromDatabase(id);
        verifyNoMoreInteractions(activities);
    }
}
