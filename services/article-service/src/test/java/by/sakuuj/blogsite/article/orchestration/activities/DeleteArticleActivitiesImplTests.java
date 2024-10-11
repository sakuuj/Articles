package by.sakuuj.blogsite.article.orchestration.activities;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.exception.EntityNotFoundException;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.service.IdempotencyTokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteArticleActivitiesImplTests {

    @Mock
    private ElasticsearchEventProducer elasticsearchEventProducer;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private IdempotencyTokenService idempotencyTokenService;

    @InjectMocks
    private DeleteArticleActivitiesImpl deleteArticleActivities;

    @Nested
    class deleteFromDatabase {

        @Test
        void shouldDeleteFromRepoAndIdempotencyService_WhenPresent() {

            // given
            UUID idToDeleteBy = UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd");

            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleEntity article = testDataBuilder.build();

            when(articleRepository.findById(any())).thenReturn(Optional.of(article));

            doNothing().when(articleRepository).deleteById(any());

            doNothing().when(idempotencyTokenService).deleteByCreationId(any());

            // when
            deleteArticleActivities.deleteFromDatabase(idToDeleteBy);

            // then
            InOrder inOrder = inOrder(
                    articleRepository,
                    idempotencyTokenService
            );

            inOrder.verify(articleRepository).findById(idToDeleteBy);
            inOrder.verify(articleRepository).deleteById(idToDeleteBy);

            CreationId expectedCreationId = CreationId.of(ArticleEntity.class, idToDeleteBy);
            inOrder.verify(idempotencyTokenService).deleteByCreationId(expectedCreationId);

            inOrder.verifyNoMoreInteractions();
        }

        @Test
        void shouldNotDeleteFromRepoAndIdempotencyService_WhenNotPresent() {

            // given
            UUID idToDeleteBy = UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd");

            when(articleRepository.findById(any())).thenReturn(Optional.empty());

            // when
            Assertions.assertThatThrownBy(() -> deleteArticleActivities.deleteFromDatabase(idToDeleteBy))
                    .isInstanceOf(EntityNotFoundException.class);

            // then
            verify(articleRepository).findById(idToDeleteBy);
            verifyNoMoreInteractions(articleRepository);

            verifyNoInteractions(idempotencyTokenService);
        }
    }

    @Nested
    class sendDeleteDocumentEvent {

        @Test
        void shouldSendDeleteDocumentEvent() {

            // given
            UUID id = ArticleTestDataBuilder.anArticle().getId();

            doNothing().when(elasticsearchEventProducer).produce(any(), any());

            // when
            deleteArticleActivities.sendDeleteDocumentEvent(id);

            // then
            verify(elasticsearchEventProducer).produce(ArticleDocumentRequest.RequestType.DELETE, null);
        }
    }
}
