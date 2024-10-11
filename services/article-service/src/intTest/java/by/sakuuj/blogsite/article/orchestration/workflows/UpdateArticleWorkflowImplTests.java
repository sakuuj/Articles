package by.sakuuj.blogsite.article.orchestration.workflows;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.orchestration.activities.UpdateArticleActivities;
import by.sakuuj.blogsite.article.orchestration.workflows.UpdateArticleWorkflow;
import by.sakuuj.blogsite.article.orchestration.workflows.UpdateArticleWorkflowImpl;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class UpdateArticleWorkflowImplTests {

    @RegisterExtension
    private static final TestWorkflowExtension testWorkflowExtension = TestWorkflowExtension.newBuilder()
            .registerWorkflowImplementationTypes(UpdateArticleWorkflowImpl.class)
            .setDoNotStart(true)
            .build();

    @AfterEach
    void closeWorkflowEnv(TestWorkflowEnvironment workflowEnv) {
        workflowEnv.shutdown();
    }

    @Test
    void shouldUpdateInDatabase_AndThenSendDocumentEvent_WhenNoErrors(
            TestWorkflowEnvironment workflowEnv,
            UpdateArticleWorkflow workflow,
            Worker worker
    ) {
        UpdateArticleActivities activities = Mockito.mock(UpdateArticleActivities.class, withSettings().withoutAnnotations());

        ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
        ArticleRequest articleRequest = articleBuilder.buildRequest();
        UUID id = articleBuilder.getId();
        short version = articleBuilder.getVersion();

        ArticleResponse expected = articleBuilder.buildResponse();

        when(activities.updateByIdInDatabase(any(), any(), anyShort()))
                .thenReturn(expected);

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        ArticleResponse actual = workflow.updateArticle(articleRequest, id, version);

        assertThat(actual).isEqualTo(expected);

        InOrder inOrder = Mockito.inOrder(activities);
        inOrder.verify(activities).updateByIdInDatabase(articleRequest, id, version);
        inOrder.verify(activities).sendUpdateDocumentEvent(expected);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotProceed_WhenCanNotUpdateInDatabase(
            TestWorkflowEnvironment workflowEnv,
            UpdateArticleWorkflow workflow,
            Worker worker
    ) {
        UpdateArticleActivities activities = Mockito.mock(UpdateArticleActivities.class, withSettings().withoutAnnotations());

        ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
        ArticleRequest articleRequest = articleBuilder.buildRequest();
        UUID id = articleBuilder.getId();
        short version = articleBuilder.getVersion();

        doThrow(new RuntimeException("ERROR")).when(activities).updateByIdInDatabase(any(), any(), anyShort());

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        assertThatThrownBy(() -> workflow.updateArticle(articleRequest, id, version))
                .isInstanceOf(WorkflowFailedException.class);

        verify(activities, times(1)).updateByIdInDatabase(articleRequest, id, version);
        verifyNoMoreInteractions(activities);
    }
}
