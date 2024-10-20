package by.sakuuj.articles.article.orchestration.workflows;

import by.sakuuj.articles.article.ArticleTestDataBuilder;
import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.orchestration.activities.CreateArticleActivities;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class CreateArticleWorkflowImplTests {

    @RegisterExtension
    private static final TestWorkflowExtension testWorkflowExtension = TestWorkflowExtension.newBuilder()
            .registerWorkflowImplementationTypes(CreateArticleWorkflowImpl.class)
            .setDoNotStart(true)
            .build();

    @AfterEach
    void closeWorkflowEnv(TestWorkflowEnvironment workflowEnv) {
        workflowEnv.shutdown();
    }

    @Test
    void shouldSaveInDatabase_AndThenSendDocumentEvent_WhenNoErrors(
            TestWorkflowEnvironment workflowEnv,
            CreateArticleWorkflow workflow,
            Worker worker
    ) {
        CreateArticleActivities activities = Mockito.mock(CreateArticleActivities.class, withSettings().withoutAnnotations());

        ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
        ArticleRequest articleRequest = articleBuilder.buildRequest();
        IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                .idempotencyTokenValue(UUID.fromString("cef9d95f-2197-4d4d-82eb-a7ab5090eccf"))
                .clientId(UUID.fromString("453060c1-e4fe-4a18-9075-74d89252f84e"))
                .build();

        ArticleResponse expected = articleBuilder.buildResponse();

        when(activities.saveInDatabase(any(), any()))
                .thenReturn(expected);

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        ArticleResponse actual = workflow.createArticle(articleRequest, idempotencyTokenId);

        assertThat(actual).isEqualTo(expected);

        InOrder inOrder = Mockito.inOrder(activities);
        inOrder.verify(activities).saveInDatabase(articleRequest, idempotencyTokenId);
        inOrder.verify(activities).sendSaveDocumentEvent(expected);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotProceed_WhenCanNotSaveInDatabase(
            TestWorkflowEnvironment workflowEnv,
            CreateArticleWorkflow workflow,
            Worker worker
    ) {
        CreateArticleActivities activities = Mockito.mock(CreateArticleActivities.class, withSettings().withoutAnnotations());

        ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();
        ArticleRequest articleRequest = articleBuilder.buildRequest();
        IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                .idempotencyTokenValue(UUID.fromString("cef9d95f-2197-4d4d-82eb-a7ab5090eccf"))
                .clientId(UUID.fromString("453060c1-e4fe-4a18-9075-74d89252f84e"))
                .build();

        doThrow(new RuntimeException("ERROR")).when(activities).saveInDatabase(articleRequest, idempotencyTokenId);

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        assertThatThrownBy(() -> workflow.createArticle(articleRequest, idempotencyTokenId))
                .isInstanceOf(WorkflowFailedException.class);

        verify(activities, times(1)).saveInDatabase(articleRequest, idempotencyTokenId);

        verifyNoMoreInteractions(activities);
    }
}
