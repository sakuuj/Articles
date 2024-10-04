package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.ArticleTestDataBuilder;
import by.sakuuj.blogsite.article.PagingTestDataBuilder;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.validator.DtoValidator;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.exception.ServiceLayerException;
import by.sakuuj.blogsite.article.exception.ServiceLayerExceptionMessage;
import by.sakuuj.blogsite.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.blogsite.article.mapper.jpa.ArticleMapper;
import by.sakuuj.blogsite.article.repository.elasticsearch.ArticleDocumentRepository;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.article.repository.jpa.ArticleTopicRepository;
import by.sakuuj.blogsite.article.service.authorization.ArticleServiceAuthorizer;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import by.sakuuj.blogsite.service.IdempotencyTokenService;
import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTests {

    @Spy
    private TransactionTemplate txTemplate;

    @Mock
    private TransactionStatus txStatus;

    @BeforeEach
    public void configureTxTemplate() {

        lenient().doAnswer(invocation ->
                {
                    TransactionCallback<?> argument = invocation.getArgument(0, TransactionCallback.class);
                    return argument.doInTransaction(txStatus);
                })
                .when(txTemplate).execute(any());

        lenient().doNothing().when(txTemplate).setReadOnly(anyBoolean());
    }

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleDocumentRepository articleDocumentRepository;

    @Mock
    private ArticleDocumentMapper articleDocumentMapper;

    @Mock
    private ArticleTopicRepository articleTopicRepository;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private ArticleServiceAuthorizer articleServiceAuthorizer;

    @Mock
    private DtoValidator dtoValidator;

    @Mock
    private IdempotencyTokenService idempotencyTokenService;

    @InjectMocks
    private ArticleServiceImpl articleServiceImpl;

    @Nested
    class findById_UUID {

        @Test
        void shouldFindById_WhenEntityIsPresent() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            UUID idToFindBy = testDataBuilder.getId();
            ArticleEntity expectedEntityFromRepo = testDataBuilder.build();
            ArticleResponse expectedResponseFromMapper = testDataBuilder.buildResponse();

            when(articleRepository.findById(idToFindBy))
                    .thenReturn(Optional.of(expectedEntityFromRepo));
            when(articleMapper.toResponse(expectedEntityFromRepo))
                    .thenReturn(expectedResponseFromMapper);

            // when
            Optional<ArticleResponse> actual = articleServiceImpl.findById(idToFindBy);

            // then
            assertThat(actual).isPresent();
            assertThat(actual.get())
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponseFromMapper);

            verify(articleRepository).findById(idToFindBy);
            verifyNoMoreInteractions(articleRepository);

            verify(articleMapper).toResponse(expectedEntityFromRepo);
            verifyNoMoreInteractions(articleMapper);

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }

        @Test
        void shouldNotFindById_WhenEntityIsNotPresent() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            UUID idToFindBy = testDataBuilder.getId();

            when(articleRepository.findById(idToFindBy))
                    .thenReturn(Optional.empty());

            // when
            Optional<ArticleResponse> actual = articleServiceImpl.findById(idToFindBy);

            // then
            assertThat(actual).isNotPresent();

            verify(articleRepository).findById(idToFindBy);
            verifyNoMoreInteractions(articleRepository);

            verifyNoMoreInteractions(articleMapper);

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }
    }


    @Nested
    class findAllSortedByCreatedAtDesc_RequestedPage {

        @Captor
        private ArgumentCaptor<Pageable> pageableArgumentCaptor;

        @Test
        void shouldSetPageAndSortByCreatedAtDesc() {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

            when(articleRepository.findAll(any(Pageable.class)))
                    .thenReturn(pagingBuilder.emptySlice());

            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            // when
            articleServiceImpl.findAllSortedByCreatedAtDesc(requestedPage);

            // then
            verify(articleRepository).findAll(pageableArgumentCaptor.capture());
            Pageable pageable = pageableArgumentCaptor.getValue();

            assertThat(pageable.getPageNumber()).isEqualTo(requestedPage.number());
            assertThat(pageable.getPageSize()).isEqualTo(requestedPage.size());
            Sort sort = pageable.getSort();

            assertThat(sort.isSorted()).isTrue();

            Sort.Order createdAtOrder = sort.getOrderFor("modificationAudit.createdAt");
            assertThat(createdAtOrder).isNotNull();
            assertThat(createdAtOrder.isDescending()).isTrue();
        }

        @Test
        void shouldSearchInRepo_ThenMap() {

            // given
            ArticleTestDataBuilder firstTestDataBuilder = ArticleTestDataBuilder.anArticle();
            ArticleTestDataBuilder secondTestDataBuilder = ArticleTestDataBuilder.anArticle()
                    .withId(UUID.fromString("1177cd58-6ab3-4d20-87f5-932c91ce1fbe"));

            ArticleEntity firstExpectedFromRepo = firstTestDataBuilder.build();
            ArticleResponse firstExpectedFromMapper = firstTestDataBuilder.buildResponse();

            ArticleEntity secondExpectedFromRepo = secondTestDataBuilder.build();
            ArticleResponse secondExpectedFromMapper = secondTestDataBuilder.buildResponse();

            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

            Slice<ArticleEntity> expectedSlice = pagingBuilder.aSlice(
                    List.of(firstExpectedFromRepo, secondExpectedFromRepo)
            );

            when(articleRepository.findAll(any(Pageable.class)))
                    .thenReturn(expectedSlice);

            when(articleMapper.toResponse(firstExpectedFromRepo))
                    .thenReturn(firstExpectedFromMapper);

            when(articleMapper.toResponse(secondExpectedFromRepo))
                    .thenReturn(secondExpectedFromMapper);

            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            // when
            PageView<ArticleResponse> actual = articleServiceImpl.findAllSortedByCreatedAtDesc(requestedPage);

            // then
            assertThat(actual.content()).containsExactly(firstExpectedFromMapper, secondExpectedFromMapper);
            assertThat(actual.number()).isEqualTo(expectedSlice.getNumber());
            assertThat(actual.size()).isEqualTo(expectedSlice.getSize());

            verify(articleRepository).findAll(any());
            verify(articleMapper, times(2)).toResponse(any());

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }
    }


    @Nested
    class findAllByTopicsSortedByCreatedAtDesc_List$TopicRequest$_RequestedPage {

        @Test
        void shouldSearchInRepo_ThenMap() {

            // given
            ArticleTestDataBuilder firstTestDataBuilder = ArticleTestDataBuilder.anArticle();
            ArticleTestDataBuilder secondTestDataBuilder = ArticleTestDataBuilder
                    .anArticle()
                    .withId(UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd"));

            ArticleEntity firstArticle = firstTestDataBuilder.build();
            ArticleEntity secondArticle = secondTestDataBuilder.build();

            ArticleResponse firstResponse = firstTestDataBuilder.buildResponse();
            ArticleResponse secondResponse = secondTestDataBuilder.buildResponse();

            List<TopicRequest> topics = firstTestDataBuilder.getTopicRequests();
            List<String> topicNames = firstTestDataBuilder.getTopicNames();

            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

            PageView<ArticleEntity> expectedPageView = pagingBuilder.aPageView(List.of(firstArticle, secondArticle));
            when(articleRepository.findAllByTopicsAndSortByCreatedAtDesc(anyList(), any(RequestedPage.class)))
                    .thenReturn(expectedPageView);

            when(articleMapper.toResponse(firstArticle))
                    .thenReturn(firstResponse);

            when(articleMapper.toResponse(secondArticle))
                    .thenReturn(secondResponse);

            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            // when
            PageView<ArticleResponse> actual = articleServiceImpl.findAllByTopicsSortedByCreatedAtDesc(
                    topics,
                    requestedPage
            );

            // then
            assertThat(actual.content()).containsExactly(firstResponse, secondResponse);
            assertThat(actual.number()).isEqualTo(requestedPage.number());
            assertThat(actual.size()).isEqualTo(requestedPage.size());

            verify(articleRepository).findAllByTopicsAndSortByCreatedAtDesc(topicNames, requestedPage);
            verifyNoMoreInteractions(articleRepository);

            verify(articleMapper, times(2)).toResponse(any());
            verifyNoMoreInteractions(articleMapper);

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }

        @Test
        void shouldNotMap_whenNotFoundInRepo() {

            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

            when(articleRepository.findAllByTopicsAndSortByCreatedAtDesc(anyList(), any(RequestedPage.class)))
                    .thenReturn(pagingBuilder.emptyPageView());

            List<TopicRequest> topics = List.of();
            List<String> topicNames = List.of();

            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            // when
            PageView<ArticleResponse> actual = articleServiceImpl.findAllByTopicsSortedByCreatedAtDesc(
                    topics,
                    requestedPage);

            // then
            assertThat(actual.content()).isEmpty();
            assertThat(actual.number()).isEqualTo(requestedPage.number());
            assertThat(actual.size()).isEqualTo(requestedPage.size());

            verify(articleRepository).findAllByTopicsAndSortByCreatedAtDesc(topicNames, requestedPage);

            verifyNoInteractions(articleMapper);

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }
    }


    @Nested
    class findAllBySearchTermsSortedByRelevance_String_RequestedPage {

        @Test
        void shouldSearchIdsInDocRepo_ThenSearchInRepo_ThenMap() {

            // given
            String searchTerms = "some search terms";

            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            Pageable pageable = pagingBuilder.aPageable();

            UUID firstId = UUID.fromString("4fb60217-fa21-4442-abab-3b9818b12437");
            UUID secondId = UUID.fromString("be44813c-5f2a-4d6f-a300-ae6e50862825");

            ArticleTestDataBuilder firstArticleBuilder = ArticleTestDataBuilder
                    .anArticle()
                    .withId(firstId);

            ArticleTestDataBuilder secondArticleBuilder = ArticleTestDataBuilder
                    .anArticle()
                    .withId(secondId);

            ArticleEntity firstArticle = firstArticleBuilder.build();
            ArticleEntity secondArticle = secondArticleBuilder.build();

            ArticleResponse firstResponse = firstArticleBuilder.buildResponse();
            ArticleResponse secondResponse = secondArticleBuilder.buildResponse();

            PageView<UUID> idsFoundInDocRepo = pagingBuilder.aPageView(List.of(
                    firstId, secondId
            ));

            when(articleDocumentRepository.findIdsOfDocsSortedByRelevance(any(), any()))
                    .thenReturn(idsFoundInDocRepo);

            when(articleRepository.findAllByIdsInOrder(anyList()))
                    .thenReturn(List.of(firstArticle, secondArticle));

            when(articleMapper.toResponse(firstArticle))
                    .thenReturn(firstResponse);

            when(articleMapper.toResponse(secondArticle))
                    .thenReturn(secondResponse);

            // when
            PageView<ArticleResponse> actual = articleServiceImpl.findAllBySearchTermsSortedByRelevance(searchTerms, requestedPage);

            // then
            assertThat(actual.content()).containsExactly(firstResponse, secondResponse);
            assertThat(actual.size()).isEqualTo(requestedPage.size());
            assertThat(actual.number()).isEqualTo(requestedPage.number());

            verify(articleDocumentRepository).findIdsOfDocsSortedByRelevance(searchTerms, pageable);
            verifyNoMoreInteractions(articleRepository);

            verify(txTemplate).execute(any());
            verifyNoMoreInteractions(txTemplate);

            verify(articleRepository).findAllByIdsInOrder(List.of(firstId, secondId));
            verifyNoMoreInteractions(articleRepository);

            verify(articleMapper, times(2)).toResponse(any());
            verifyNoMoreInteractions(articleMapper);

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper
            );
        }

        @Test
        void shouldNotStartTransaction_WhenNotFoundInDocRepo() {

            // given
            String searchTerms = "some search terms";

            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();

            RequestedPage requestedPage = pagingBuilder.aRequestedPage();
            Pageable pageable = pagingBuilder.aPageable();

            when(articleDocumentRepository.findIdsOfDocsSortedByRelevance(any(), any()))
                    .thenReturn(
                            PageView.<UUID>empty()
                                    .withNumberAndSize(requestedPage)
                    );

            // when
            PageView<ArticleResponse> actual = articleServiceImpl.findAllBySearchTermsSortedByRelevance(searchTerms, requestedPage);

            // then
            assertThat(actual.content()).isEmpty();
            assertThat(actual.size()).isEqualTo(requestedPage.size());
            assertThat(actual.number()).isEqualTo(requestedPage.number());

            verify(articleDocumentRepository).findIdsOfDocsSortedByRelevance(searchTerms, pageable);
            verifyNoMoreInteractions(articleDocumentRepository);

            verifyNoInteractions(txTemplate);
            verifyNoInteractions(articleRepository);
            verifyNoInteractions(articleMapper);

            verifyNoInteractions(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper
            );
        }
    }


    @Nested
    class deleteById_UUID_AuthenticatedUser {

        @Test
        void shouldDeleteFromRepoAndIdempotencyService_WhenPresent() {

            // given
            UUID idToDeleteBy = UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd");

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                    .build();

            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleEntity article = testDataBuilder.build();

            doNothing().when(articleServiceAuthorizer).authorizeDeleteById(any(), any());

            when(articleRepository.findById(any())).thenReturn(Optional.of(article));

            doNothing().when(articleRepository).deleteById(any());

            doNothing().when(idempotencyTokenService).deleteByCreationId(any());

            doNothing().when(articleDocumentRepository).deleteById(any());

            // when
            articleServiceImpl.deleteById(idToDeleteBy, authenticatedUser);

            // then
            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    articleRepository,
                    idempotencyTokenService,
                    articleDocumentRepository
            );

            inOrder.verify(articleServiceAuthorizer).authorizeDeleteById(eq(idToDeleteBy), same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(articleRepository).findById(idToDeleteBy);
            inOrder.verify(articleRepository).deleteById(idToDeleteBy);
            verifyNoMoreInteractions(articleRepository);

            CreationId expectedCreationId = CreationId.of(ArticleEntity.class, idToDeleteBy);
            inOrder.verify(idempotencyTokenService).deleteByCreationId(expectedCreationId);
            verifyNoMoreInteractions(idempotencyTokenService);

            inOrder.verify(articleDocumentRepository).deleteById(idToDeleteBy);
            verifyNoMoreInteractions(articleDocumentRepository);

            verifyNoInteractions(
                    dtoValidator,
                    articleDocumentMapper
            );
        }

        @Test
        void shouldNotDeleteFromRepoAndIdempotencyService_WhenNotPresent() {

            // given
            UUID idToDeleteBy = UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd");

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                    .build();

            doNothing().when(articleServiceAuthorizer).authorizeDeleteById(any(), any());

            when(articleRepository.findById(any()))
                    .thenReturn(Optional.empty());

            // when
            articleServiceImpl.deleteById(idToDeleteBy, authenticatedUser);

            // then
            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    articleRepository
            );

            inOrder.verify(articleServiceAuthorizer).authorizeDeleteById(eq(idToDeleteBy), same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(articleRepository).findById(idToDeleteBy);
            verifyNoMoreInteractions(articleRepository);

            verifyNoInteractions(idempotencyTokenService);

            verifyNoInteractions(articleDocumentRepository);

            verifyNoInteractions(
                    dtoValidator,
                    articleDocumentMapper
            );
        }
    }


    @Nested
    class updateById_UUID_ArticleRequest_short_AuthenticatedUser {

        @Test
        void shouldFindInRepo_AndThenUpdateUsingMapper_OnCorrectVersion() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = testDataBuilder.buildRequest();
            ArticleDocument updatedArticleDocument = testDataBuilder.buildDocument();

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                    .build();

            ArticleEntity oldArticleEntity = testDataBuilder
                    .withContent("old content")
                    .withTitle("old title")
                    .build();

            UUID articleId = oldArticleEntity.getId();
            short version = oldArticleEntity.getVersion();

            doNothing().when(articleServiceAuthorizer).authorizeUpdateById(any(), any());

            doNothing().when(dtoValidator).validate(any());

            when(articleRepository.findById(any())).thenReturn(Optional.of(oldArticleEntity));

            doNothing().when(articleMapper).updateEntity(any(), any());

            when(articleDocumentMapper.toDocument(any())).thenReturn(updatedArticleDocument);
            when(articleDocumentRepository.save(any())).thenReturn(updatedArticleDocument);

            // when
            articleServiceImpl.updateById(articleId, articleRequest, version, authenticatedUser);

            // then
            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    dtoValidator,
                    articleRepository,
                    articleMapper,
                    articleDocumentMapper,
                    articleDocumentRepository
            );

            inOrder.verify(articleServiceAuthorizer).authorizeUpdateById(eq(articleId), same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(dtoValidator).validate(articleRequest);
            verifyNoMoreInteractions(dtoValidator);

            inOrder.verify(articleRepository).findById(articleId);
            verifyNoMoreInteractions(articleRepository);

            inOrder.verify(articleMapper).updateEntity(oldArticleEntity, articleRequest);
            verifyNoMoreInteractions(articleMapper);

            inOrder.verify(articleDocumentMapper).toDocument(oldArticleEntity);
            verifyNoMoreInteractions(articleDocumentMapper);

            inOrder.verify(articleDocumentRepository).save(updatedArticleDocument);
            verifyNoMoreInteractions(articleDocumentRepository);

            inOrder.verifyNoMoreInteractions();

            verifyNoInteractions(
                    idempotencyTokenService
            );
        }

        @Test
        void shouldThrowExceptionAndNotMap_WhenIncorrectVersionDetectedComparingToFoundFromRepo() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = testDataBuilder.buildRequest();

            UUID articleId = testDataBuilder.getId();
            short version = 234;
            short incorrectVersion = 646;

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            ArticleEntity oldArticleEntity = testDataBuilder
                    .withContent("old content")
                    .withTitle("old title")
                    .withVersion(version)
                    .build();

            doNothing().when(articleServiceAuthorizer).authorizeUpdateById(any(), any());

            doNothing().when(dtoValidator).validate(articleRequest);

            when(articleRepository.findById(any()))
                    .thenReturn(Optional.of(oldArticleEntity));

            // when, then
            assertThatThrownBy(() -> articleServiceImpl.updateById(articleId, articleRequest, incorrectVersion, authenticatedUser))
                    .isInstanceOfSatisfying(ServiceLayerException.class, ex ->
                    {
                        assertThat(ex.getServiceLayerExceptionMessage())
                                .isEqualTo(ServiceLayerExceptionMessage.OPERATION_FAILED_ENTITY_VERSION_DOES_NOT_MATCH);

                    });

            InOrder inOrder = inOrder(articleServiceAuthorizer, dtoValidator, articleRepository);

            inOrder.verify(articleServiceAuthorizer).authorizeUpdateById(eq(articleId), same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(dtoValidator).validate(articleRequest);
            verifyNoMoreInteractions(dtoValidator);

            inOrder.verify(articleRepository).findById(articleId);
            verifyNoMoreInteractions(articleRepository);

            verifyNoInteractions(articleMapper);

            verifyNoInteractions(articleDocumentMapper);
            verifyNoInteractions(articleDocumentRepository);

            verifyNoInteractions(
                    idempotencyTokenService
            );
        }

        @Test
        void shouldThrowExceptionAndNotMap_IfEntityNotFound() {

            // given
            ArticleTestDataBuilder testDataBuilder = ArticleTestDataBuilder.anArticle();

            ArticleRequest articleRequest = testDataBuilder.buildRequest();

            UUID articleId = testDataBuilder.getId();
            short version = 234;

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            doNothing().when(articleServiceAuthorizer).authorizeUpdateById(any(), any());

            doNothing().when(dtoValidator).validate(any());

            when(articleRepository.findById(any()))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> articleServiceImpl.updateById(articleId, articleRequest, version, authenticatedUser))
                    .isInstanceOfSatisfying(ServiceLayerException.class, ex ->
                    {
                        assertThat(ex.getServiceLayerExceptionMessage())
                                .isEqualTo(ServiceLayerExceptionMessage.UPDATE_FAILED_ENTITY_NOT_FOUND);

                    });

            InOrder inOrder = inOrder(articleServiceAuthorizer, dtoValidator, articleRepository);

            inOrder.verify(articleServiceAuthorizer).authorizeUpdateById(eq(articleId), same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(dtoValidator).validate(articleRequest);
            verifyNoMoreInteractions(dtoValidator);

            inOrder.verify(articleRepository).findById(articleId);
            verifyNoMoreInteractions(articleRepository);

            verifyNoInteractions(articleMapper);

            verifyNoInteractions(articleDocumentMapper);
            verifyNoInteractions(articleDocumentRepository);

            verifyNoInteractions(
                    idempotencyTokenService
            );
        }

    }


    @Nested
    class create_ArticleRequest_UUID_AuthenticatedUser {

        @Test
        void shouldCheckIfIdempotencyTokenExists_andShouldNotCreateIfItDoes() {

            // given
            var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
            var personTestDataBuilder = PersonTestDataBuilder.aPerson();

            ArticleRequest articleToCreate = articleTestDataBuilder.buildRequest();

            UUID authorId = personTestDataBuilder.getId();
            UUID idempotencyTokenValue = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");
            var authenticatedUser = AuthenticatedUser.builder().build();
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(authorId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();

            doNothing().when(articleServiceAuthorizer).authorizeCreate(any());
            doNothing().when(dtoValidator).validate(any());

            IdempotencyTokenEntity existingToken = IdempotencyTokenEntity.builder().build();
            when(idempotencyTokenService.findById(any())).thenReturn(Optional.of(existingToken));

            // when, then
            assertThatThrownBy(() -> articleServiceImpl.create(
                    articleToCreate,
                    authorId,
                    idempotencyTokenValue,
                    authenticatedUser
            )).isInstanceOfSatisfying(ServiceLayerException.class, ex ->
            {
                assertThat(ex.getServiceLayerExceptionMessage())
                        .isEqualTo(ServiceLayerExceptionMessage.CREATE_FAILED_IDEMPOTENCY_TOKEN_ALREADY_EXISTS);
            });

            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService
            );

            inOrder.verify(articleServiceAuthorizer).authorizeCreate(same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(dtoValidator).validate(articleToCreate);
            verifyNoMoreInteractions(dtoValidator);

            inOrder.verify(idempotencyTokenService).findById(idempotencyTokenId);
            verifyNoMoreInteractions(idempotencyTokenService);

            verifyNoInteractions(articleRepository);
            verifyNoInteractions(articleMapper);

            verifyNoInteractions(articleDocumentMapper);
            verifyNoInteractions(articleDocumentRepository);
        }

        @Test
        void shouldCheckIfIdempotencyTokenExists_ThenMapToEntity_ThenSaveInRepo_ThenSaveIdempotencyToken() {

            // given
            var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
            var personTestDataBuilder = PersonTestDataBuilder.aPerson();

            ArticleRequest articleToCreateRequest = articleTestDataBuilder.buildRequest();
            ArticleEntity articleToCreate = articleTestDataBuilder.build();
            ArticleDocument articleDocumentToCreate = articleTestDataBuilder.buildDocument();

            UUID authorId = personTestDataBuilder.getId();
            UUID idempotencyTokenValue = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");
            var authenticatedUser = AuthenticatedUser.builder().build();
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(authorId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();
            var creationId = CreationId.of(ArticleEntity.class, articleToCreate.getId());

            doNothing().when(articleServiceAuthorizer).authorizeCreate(any());
            doNothing().when(dtoValidator).validate(any());

            when(idempotencyTokenService.findById(any()))
                    .thenReturn(Optional.empty());
            when(articleMapper.toEntity(any(), any()))
                    .thenReturn(articleToCreate);
            when(articleRepository.save(any()))
                    .thenReturn(articleToCreate);
            doNothing().when(idempotencyTokenService).create(any(), any());

            when(articleDocumentMapper.toDocument(any())).thenReturn(articleDocumentToCreate);
            when(articleDocumentRepository.save(any())).thenReturn(articleDocumentToCreate);

            // when
            articleServiceImpl.create(
                    articleToCreateRequest,
                    authorId,
                    idempotencyTokenValue,
                    authenticatedUser
            );

            // then
            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    articleMapper,
                    articleRepository,
                    articleDocumentMapper,
                    articleDocumentRepository
            );

            inOrder.verify(articleServiceAuthorizer).authorizeCreate(same(authenticatedUser));
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(dtoValidator).validate(articleToCreateRequest);
            verifyNoMoreInteractions(dtoValidator);

            inOrder.verify(idempotencyTokenService).findById(idempotencyTokenId);

            inOrder.verify(articleMapper).toEntity(articleToCreateRequest, authorId);
            verifyNoMoreInteractions(articleMapper);

            inOrder.verify(articleRepository).save(articleToCreate);
            verifyNoMoreInteractions(articleRepository);

            inOrder.verify(idempotencyTokenService).create(idempotencyTokenId, creationId);
            verifyNoMoreInteractions(idempotencyTokenService);

            inOrder.verify(articleDocumentMapper).toDocument(articleToCreate);
            verifyNoMoreInteractions(articleDocumentMapper);

            inOrder.verify(articleDocumentRepository).save(articleDocumentToCreate);
            verifyNoMoreInteractions(articleDocumentRepository);

            inOrder.verifyNoMoreInteractions();
        }
    }

    @Nested
    class addTopic_UUID_UUID_AuthenticatedUser {

        @Test
        void shouldAddTopic() {

            // given
            var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
            var topicTestDataBuilder = TopicTestDataBuilder.aTopic();

            UUID topicId = topicTestDataBuilder.getId();
            UUID articleId = articleTestDataBuilder.getId();

            ArticleTopicId articleTopicId = ArticleTopicId.builder()
                    .topicId(topicId)
                    .articleId(articleId)
                    .build();

            var authenticatedUser = AuthenticatedUser.builder().build();

            doNothing().when(articleServiceAuthorizer).authorizeAddTopic(any(), any());

            when(articleTopicRepository.save(any()))
                    .thenReturn(null);

            // when
            articleServiceImpl.addTopic(topicId, articleId, authenticatedUser);

            // then
            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    articleTopicRepository
            );

            inOrder.verify(articleServiceAuthorizer).authorizeAddTopic(articleId, authenticatedUser);
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(articleTopicRepository).save(articleTopicId);
            verifyNoMoreInteractions(articleTopicRepository);

            verifyNoInteractions(
                    articleMapper,
                    articleRepository,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }
    }

    @Nested
    class removeTopic_UUID_UUID_AuthenticatedUser {

        @Test
        void shouldRemoveTopic() {

            // given
            var articleTestDataBuilder = ArticleTestDataBuilder.anArticle();
            var topicTestDataBuilder = TopicTestDataBuilder.aTopic();

            UUID topicId = topicTestDataBuilder.getId();
            UUID articleId = articleTestDataBuilder.getId();

            ArticleTopicId articleTopicId = ArticleTopicId.builder()
                    .topicId(topicId)
                    .articleId(articleId)
                    .build();

            var authenticatedUser = AuthenticatedUser.builder().build();

            doNothing().when(articleServiceAuthorizer).authorizeAddTopic(any(), any());

            doNothing().when(articleTopicRepository).removeById(any());

            // when
            articleServiceImpl.removeTopic(topicId, articleId, authenticatedUser);

            // then
            InOrder inOrder = inOrder(
                    articleServiceAuthorizer,
                    articleTopicRepository
            );

            inOrder.verify(articleServiceAuthorizer).authorizeAddTopic(articleId, authenticatedUser);
            verifyNoMoreInteractions(articleServiceAuthorizer);

            inOrder.verify(articleTopicRepository).removeById(articleTopicId);
            verifyNoMoreInteractions(articleTopicRepository);

            verifyNoInteractions(
                    articleMapper,
                    articleRepository,
                    dtoValidator,
                    idempotencyTokenService,
                    articleDocumentMapper,
                    articleDocumentRepository
            );
        }
    }
}