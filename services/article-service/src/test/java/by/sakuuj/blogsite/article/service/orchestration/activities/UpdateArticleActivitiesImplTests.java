package by.sakuuj.blogsite.article.service.orchestration.activities;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.exception.EntityNotFoundException;
import by.sakuuj.blogsite.article.exception.EntityVersionDoesNotMatch;
import by.sakuuj.blogsite.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.blogsite.article.mapper.jpa.ArticleMapper;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateArticleActivitiesImplTests {

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private ArticleDocumentMapper articleDocumentMapper;

    @Mock
    private TransactionTemplate txTemplate;

    @BeforeEach
    public void configureTxTemplate() {

        TransactionStatus txStatus = Mockito.mock(TransactionStatus.class);

        lenient().doAnswer(invocation ->
                {
                    TransactionCallback<?> argument = invocation.getArgument(0, TransactionCallback.class);
                    return argument.doInTransaction(txStatus);
                })
                .when(txTemplate).execute(any());
    }


    @Mock
    private ElasticsearchEventProducer elasticsearchEventProducer;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private UpdateArticleActivitiesImpl updateArticleActivities;

    @Nested
    class updateByIdInDatabase {

        @Test
        void shouldFindInRepo_AndThenUpdateUsingMapper_OnCorrectVersion() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = testDataBuilder.buildRequest();
            ArticleResponse expectedResponse = testDataBuilder.buildResponse();

            ArticleEntity oldArticleEntity = testDataBuilder
                    .withContent("old content")
                    .withTitle("old title")
                    .build();

            UUID articleId = oldArticleEntity.getId();
            short version = oldArticleEntity.getVersion();


            when(articleRepository.findById(any())).thenReturn(Optional.of(oldArticleEntity));
            doNothing().when(articleMapper).updateEntity(any(), any());
            when(articleMapper.toResponse(any())).thenReturn(expectedResponse);

            // when
            updateArticleActivities.updateByIdInDatabase(articleRequest, articleId, version);

            // then
            InOrder inOrder = inOrder(
                    articleRepository,
                    articleMapper
            );

            inOrder.verify(articleRepository).findById(articleId);

            inOrder.verify(articleMapper).updateEntity(oldArticleEntity, articleRequest);
            inOrder.verify(articleMapper).toResponse(oldArticleEntity);

            inOrder.verifyNoMoreInteractions();
        }


        @Test
        void shouldThrowNotMatchingVersionException_OnNotMatchingVersion() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = testDataBuilder.buildRequest();

            UUID articleId = testDataBuilder.getId();
            short actualVersion = 234;
            short notMatchingVersion = 646;

            ArticleEntity oldArticleEntity = testDataBuilder
                    .withContent("old content")
                    .withTitle("old title")
                    .withVersion(actualVersion)
                    .build();

            when(articleRepository.findById(any()))
                    .thenReturn(Optional.of(oldArticleEntity));

            // when, then
            assertThatThrownBy(() -> updateArticleActivities.updateByIdInDatabase(articleRequest, articleId, notMatchingVersion))
                    .isInstanceOf(EntityVersionDoesNotMatch.class);

            verify(articleRepository).findById(articleId);
            verifyNoMoreInteractions(articleRepository);
        }

        @Test
        void shouldThrowNotFoundException_OnEntityNotFound() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = testDataBuilder.buildRequest();

            UUID articleId = testDataBuilder.getId();
            short version = testDataBuilder.getVersion();

            when(articleRepository.findById(any()))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> updateArticleActivities.updateByIdInDatabase(articleRequest, articleId, version))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(articleRepository).findById(articleId);
            verifyNoMoreInteractions(articleRepository);
        }

    }

    @Nested
    class sendUpdateDocumentEvent {

        @Test
        void shouldSendUpdateDocumentEvent() {

            // given
            ArticleTestDataBuilder articleBuilder = ArticleTestDataBuilder.anArticle();

            ArticleResponse articleResponse = articleBuilder.buildResponse();
            ArticleDocument articleDocument = articleBuilder.buildDocument();

            when(articleDocumentMapper.toDocument(any(ArticleResponse.class))).thenReturn(articleDocument);
            doNothing().when(elasticsearchEventProducer).produce(any(), any());

            // when
            updateArticleActivities.sendUpdateDocumentEvent(articleResponse);

            // then
            verify(articleDocumentMapper).toDocument(articleResponse);
            verifyNoMoreInteractions(articleDocumentMapper);

            verify(elasticsearchEventProducer).produce(ArticleDocumentRequest.RequestType.UPSERT, articleDocument);
            verifyNoMoreInteractions(elasticsearchEventProducer);
        }
    }
}
