package by.sakuuj.blogsite.article.service.orchestration.activities;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.exception.IdempotencyTokenExistsException;
import by.sakuuj.blogsite.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.blogsite.article.mapper.jpa.ArticleMapper;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.blogsite.service.IdempotencyTokenService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateArticleActivitiesImplTests {

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private ArticleDocumentMapper articleDocumentMapper;

    @Mock
    private ElasticsearchEventProducer elasticsearchEventProducer;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private IdempotencyTokenService idempotencyTokenService;

    @InjectMocks
    private CreateArticleActivitiesImpl createArticleActivities;


    @Nested
    class saveInDatabase {

        @Test
        void shouldCheckIfIdempotencyTokenExists_ThenMapToEntity_ThenSaveInRepo_ThenSaveIdempotencyToken() {

            // given
            var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
            var personTestDataBuilder = PersonTestDataBuilder.aPerson();

            ArticleRequest articleToCreateRequest = articleTestDataBuilder.buildRequest();
            ArticleEntity articleToCreate = articleTestDataBuilder.build();
            ArticleResponse expectedResponse = articleTestDataBuilder.buildResponse();

            UUID authorId = personTestDataBuilder.getId();
            UUID idempotencyTokenValue = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(authorId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();
            var creationId = CreationId.of(ArticleEntity.class, articleToCreate.getId());

            when(idempotencyTokenService.findById(any()))
                    .thenReturn(Optional.empty());
            when(articleMapper.toEntity(any(), any()))
                    .thenReturn(articleToCreate);
            when(articleMapper.toResponse(any()))
                    .thenReturn(expectedResponse);
            when(articleRepository.save(any()))
                    .thenReturn(articleToCreate);
            doNothing().when(idempotencyTokenService).create(any(), any());

            // when
            createArticleActivities.saveInDatabase(
                    articleToCreateRequest,
                    idempotencyTokenId
            );

            // then
            InOrder inOrder = inOrder(
                    idempotencyTokenService,
                    articleMapper,
                    articleRepository
            );

            inOrder.verify(idempotencyTokenService).findById(idempotencyTokenId);

            inOrder.verify(articleMapper).toEntity(articleToCreateRequest, authorId);

            inOrder.verify(articleRepository).save(articleToCreate);

            inOrder.verify(idempotencyTokenService).create(idempotencyTokenId, creationId);

            inOrder.verify(articleMapper).toResponse(articleToCreate);

            inOrder.verifyNoMoreInteractions();
        }

        @Test
        void shouldCheckIfIdempotencyTokenExists_andThrowIfExceptionIfDoesExist() {

            // given
            var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
            var personTestDataBuilder = PersonTestDataBuilder.aPerson();

            ArticleRequest articleToCreate = articleTestDataBuilder.buildRequest();

            UUID authorId = personTestDataBuilder.getId();
            UUID idempotencyTokenValue = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(authorId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            IdempotencyTokenEntity existingToken = IdempotencyTokenEntity.builder().build();
            when(idempotencyTokenService.findById(any())).thenReturn(Optional.of(existingToken));

            // when, then
            assertThatThrownBy(() -> createArticleActivities.saveInDatabase(
                    articleToCreate,
                    idempotencyTokenId
            )).isInstanceOf(IdempotencyTokenExistsException.class);


            verify(idempotencyTokenService).findById(idempotencyTokenId);
            verifyNoMoreInteractions(idempotencyTokenService);

            verifyNoInteractions(articleRepository);
            verifyNoInteractions(articleMapper);
        }
    }

    @Nested
    class sendSaveDocumentEvent {

        @Test
        void shouldSendSaveDocumentEvent() {

            // given
            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            ArticleResponse articleResponse = articleBuilder.buildResponse();
            ArticleDocument articleDocument = articleBuilder.buildDocument();

            when(articleDocumentMapper.toDocument(any(ArticleResponse.class))).thenReturn(articleDocument);
            doNothing().when(elasticsearchEventProducer).produce(any(), any());

            // when
            createArticleActivities.sendSaveDocumentEvent(articleResponse);

            // then
            verify(articleDocumentMapper).toDocument(articleResponse);
            verifyNoMoreInteractions(articleDocumentMapper);

            verify(elasticsearchEventProducer).produce(ArticleDocumentRequest.RequestType.UPSERT, articleDocument);
            verifyNoMoreInteractions(elasticsearchEventProducer);
        }

    }
}
