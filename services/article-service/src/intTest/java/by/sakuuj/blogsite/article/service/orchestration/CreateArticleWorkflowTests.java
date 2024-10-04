package by.sakuuj.blogsite.article.service.orchestration;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.exception.IdempotencyTokenExistsException;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import io.temporal.client.WorkflowFailedException;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class CreateArticleWorkflowTests {

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
    void shouldSaveInDatabase_AndThenInElasticsearch_WhenNoErrors(
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

        when(activities.saveInDatabase(articleRequest, idempotencyTokenId))
                .thenReturn(expected);
        doNothing().when(activities).sendElasticsearchSaveEvent(expected.id());

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        ArticleResponse actual = workflow.createArticle(articleRequest, idempotencyTokenId);

        assertThat(actual).isEqualTo(expected);
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

        verify(activities, times(5)).saveInDatabase(articleRequest, idempotencyTokenId);
    }


    public static List<RuntimeException> shouldNotRetry_OnCertainExceptions() {
        return List.of(
                new DataIntegrityViolationException("Error"),
                new IdempotencyTokenExistsException()
        );
    }

    @MethodSource
    @ParameterizedTest
    void shouldNotRetry_OnCertainExceptions(
            RuntimeException ex,
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

        doThrow(ex).when(activities)
                .saveInDatabase(articleRequest, idempotencyTokenId);

        worker.registerActivitiesImplementations(activities);

        workflowEnv.start();

        assertThatThrownBy(() -> workflow.createArticle(articleRequest, idempotencyTokenId))
                .isInstanceOf(WorkflowFailedException.class);

        verify(activities, times(1)).saveInDatabase(articleRequest, idempotencyTokenId);
        verifyNoMoreInteractions(activities);
    }


}