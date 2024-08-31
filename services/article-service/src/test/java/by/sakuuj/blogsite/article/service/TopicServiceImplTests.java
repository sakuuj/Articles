package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.PagingTestDataBuilder;
import by.sakuuj.blogsite.article.PersonTestDataBuilder;
import by.sakuuj.blogsite.article.TopicTestDataBuilder;
import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.dtos.validator.DtoValidator;
import by.sakuuj.blogsite.article.entity.jpa.CreationId;
import by.sakuuj.blogsite.article.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.article.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.blogsite.article.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.article.exception.ExceptionMessage;
import by.sakuuj.blogsite.article.exception.ServiceLayerException;
import by.sakuuj.blogsite.article.mapper.jpa.TopicMapper;
import by.sakuuj.blogsite.article.paging.PageView;
import by.sakuuj.blogsite.article.paging.RequestedPage;
import by.sakuuj.blogsite.article.repository.jpa.TopicRepository;
import by.sakuuj.blogsite.article.service.authorization.AuthenticatedUser;
import by.sakuuj.blogsite.article.service.authorization.TopicServiceAuthorizer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTests {

    @Mock
    private TopicServiceAuthorizer topicServiceAuthorizer;
    @Mock
    private DtoValidator dtoValidator;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private TopicMapper topicMapper;
    @Mock
    private IdempotencyTokenService idempotencyTokenService;

    @InjectMocks
    private TopicServiceImpl topicServiceImpl;

    @Nested
    class findById_UUID {

        @Test
        void shouldFindById_WhenEntityIsPresent() {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();

            UUID idToFindBy = topicBuilder.getId();
            TopicEntity topicEntity = topicBuilder.build();
            TopicResponse topicResponse = topicBuilder.buildResponse();

            when(topicRepository.findById(any())).thenReturn(Optional.of(topicEntity));
            when(topicMapper.toResponse(any())).thenReturn(topicResponse);

            // when
            Optional<TopicResponse> actual = topicServiceImpl.findById(idToFindBy);

            // then
            assertThat(actual).contains(topicResponse);

            verify(topicRepository).findById(idToFindBy);
            verifyNoMoreInteractions(topicRepository);

            verify(topicMapper).toResponse(topicEntity);
            verifyNoMoreInteractions(topicMapper);

            verifyNoInteractions(
                    topicServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService
            );
        }

        @Test
        void shouldNotFindById_WhenEntityIsNotPresent() {

            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();

            UUID idToFindBy = topicBuilder.getId();

            when(topicRepository.findById(any())).thenReturn(Optional.empty());

            // when
            Optional<TopicResponse> actual = topicServiceImpl.findById(idToFindBy);

            // then
            assertThat(actual).isEmpty();

            verify(topicRepository).findById(idToFindBy);
            verifyNoMoreInteractions(topicRepository);

            verifyNoInteractions(topicMapper);

            verifyNoInteractions(
                    topicServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService
            );
        }
    }


    @Nested
    class findAll_RequestedPage {

        @Captor
        ArgumentCaptor<Pageable> pageableArgumentCaptor;

        @Test
        void shouldSetPageAndSortByCreatedAtDesc() {
            // given
            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            when(topicRepository.findAll(any())).thenReturn(pagingBuilder.emptySlice());

            // when
            topicServiceImpl.findAllSortByCreatedAtDesc(requestedPage);

            // then
            verify(topicRepository).findAll(pageableArgumentCaptor.capture());
            Pageable actualPageable = pageableArgumentCaptor.getValue();
            assertThat(actualPageable.getPageSize()).isEqualTo(requestedPage.size());
            assertThat(actualPageable.getPageNumber()).isEqualTo(requestedPage.number());

            Sort sort = actualPageable.getSort();
            Sort.Order createdAtOrder = sort.getOrderFor("modificationAudit.createdAt");
            assertThat(createdAtOrder).isNotNull();
            assertThat(createdAtOrder.isDescending()).isTrue();
        }

        @Test
        void shouldSearchInRepo_ThenMap() {
            // given
            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();

            TopicTestDataBuilder firstTopicBuilder = topicBuilder
                    .withId(UUID.fromString("309affca-2e66-493e-b050-caeebff5a9c9"))
                    .withName("first topic");
            TopicTestDataBuilder secondTopicBuilder = topicBuilder
                    .withId(UUID.fromString("60f41c11-f5b0-4602-8fd1-8e0a7c347449"))
                    .withName("second topic");

            TopicEntity firstTopicEntity = firstTopicBuilder.build();
            TopicResponse firstTopicResponse = firstTopicBuilder.buildResponse();

            TopicEntity secondTopicEntity = secondTopicBuilder.build();
            TopicResponse secondTopicResponse = secondTopicBuilder.buildResponse();

            PagingTestDataBuilder pagingBuilder = PagingTestDataBuilder.aPaging();
            RequestedPage requestedPage = pagingBuilder.aRequestedPage();

            when(topicRepository.findAll(any())).thenReturn(pagingBuilder.aSlice(
                    List.of(firstTopicEntity, secondTopicEntity)
            ));
            when(topicMapper.toResponse(firstTopicEntity)).thenReturn(firstTopicResponse);
            when(topicMapper.toResponse(secondTopicEntity)).thenReturn(secondTopicResponse);

            // when
            PageView<TopicResponse> found = topicServiceImpl.findAllSortByCreatedAtDesc(requestedPage);

            // then
            assertThat(found.content()).containsExactly(firstTopicResponse, secondTopicResponse);

            verify(topicRepository).findAll(any());

            verifyNoMoreInteractions(topicRepository);

            verify(topicMapper).toResponse(firstTopicEntity);
            verify(topicMapper).toResponse(secondTopicEntity);
            verifyNoMoreInteractions(topicMapper);

            verifyNoInteractions(
                    topicServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService
            );
        }
    }

    @Nested
    class create_TopicRequest_UUID_UUID_AuthenticatedUser {

        @Test
        void shouldCreate_IfIdempotencyTokenDoesNotExist() {

            // given
            doNothing().when(topicServiceAuthorizer).authorizeCreate(any());
            doNothing().when(dtoValidator).validate(any());

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicEntity topicEntity = topicBuilder.build();
            TopicRequest topicRequest = topicBuilder.buildRequest();
            UUID expected = topicEntity.getId();

            when(topicMapper.toEntity(any(TopicRequest.class))).thenReturn(topicEntity);
            when(topicRepository.save(any())).thenReturn(topicEntity);

            var personTestDataBuilder = PersonTestDataBuilder.aPerson();

            UUID clientId = personTestDataBuilder.getId();
            UUID idempotencyTokenValue = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();
            var creationId = CreationId.of(TopicEntity.class, topicEntity.getId());

            when(idempotencyTokenService.findById(any())).thenReturn(Optional.empty());
            doNothing().when(idempotencyTokenService).create(any(), any());

            // when
            UUID actual = topicServiceImpl.create(topicRequest, clientId, idempotencyTokenValue, authenticatedUser);

            // then
            assertThat(actual).isEqualTo(expected);

            InOrder inOrder = Mockito.inOrder(
                    topicServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService,
                    topicRepository,
                    topicMapper
            );

            inOrder.verify(topicServiceAuthorizer).authorizeCreate(same(authenticatedUser));

            inOrder.verify(dtoValidator).validate(topicRequest);

            inOrder.verify(idempotencyTokenService).findById(idempotencyTokenId);

            inOrder.verify(topicMapper).toEntity(topicRequest);

            inOrder.verify(topicRepository).save(topicEntity);

            inOrder.verify(idempotencyTokenService).create(idempotencyTokenId, creationId);

            inOrder.verifyNoMoreInteractions();
        }

        @Test
        void shouldThrowException_IfIdempotencyTokenExists() {

            // given
            doNothing().when(topicServiceAuthorizer).authorizeCreate(any());
            doNothing().when(dtoValidator).validate(any());

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicEntity topicEntity = topicBuilder.build();
            TopicRequest topicRequest = topicBuilder.buildRequest();

            var personTestDataBuilder = PersonTestDataBuilder.aPerson();

            UUID clientId = personTestDataBuilder.getId();
            UUID idempotencyTokenValue = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");
            var idempotencyTokenId = IdempotencyTokenId.builder()
                    .clientId(clientId)
                    .idempotencyTokenValue(idempotencyTokenValue)
                    .build();
            var creationId = CreationId.of(TopicEntity.class, topicEntity.getId());

            IdempotencyTokenEntity idempotencyToken = IdempotencyTokenEntity.builder()
                    .id(idempotencyTokenId)
                    .creationId(creationId)
                    .build();

            when(idempotencyTokenService.findById(any())).thenReturn(Optional.of(idempotencyToken));

            // when, then
            assertThatThrownBy(() -> topicServiceImpl.create(topicRequest, clientId, idempotencyTokenValue, authenticatedUser))
                    .isInstanceOfSatisfying(ServiceLayerException.class, ex ->

                            assertThat(ex.getExceptionMessage())
                                    .isEqualTo(ExceptionMessage.CREATE_FAILED__IDEMPOTENCY_TOKEN_ALREADY_EXISTS)
                    );

            InOrder inOrder = Mockito.inOrder(
                    topicServiceAuthorizer,
                    dtoValidator,
                    idempotencyTokenService
            );

            inOrder.verify(topicServiceAuthorizer).authorizeCreate(same(authenticatedUser));

            inOrder.verify(dtoValidator).validate(topicRequest);

            inOrder.verify(idempotencyTokenService).findById(idempotencyTokenId);

            verifyNoInteractions(
                    topicRepository,
                    topicMapper
            );
        }
    }

    @Nested
    class updateById_UUID_TopicRequest_short_AuthenticatedUser {

        @Test
        void shouldThrowException_WhenEntityIsNotFound() {
            // given
            doNothing().when(topicServiceAuthorizer).authorizeUpdate(any(), any());
            doNothing().when(dtoValidator).validate(any());

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicEntity topicEntity = topicBuilder
                    .build();
            UUID topicId = topicEntity.getId();
            short topicVersion = topicEntity.getVersion();
            TopicRequest topicRequest = topicBuilder.buildRequest();

            when(topicRepository.findById(any())).thenReturn(Optional.empty());

            // when
            assertThatThrownBy(() ->
                    topicServiceImpl.updateById(topicId, topicRequest, topicVersion, authenticatedUser)
            ).isInstanceOfSatisfying(ServiceLayerException.class, ex ->

                    assertThat(ex.getExceptionMessage())
                            .isEqualTo(ExceptionMessage.UPDATE_FAILED__ENTITY_NOT_FOUND)
            );

            // then

            InOrder inOrder = Mockito.inOrder(
                    topicServiceAuthorizer,
                    dtoValidator,
                    topicRepository
            );

            inOrder.verify(topicServiceAuthorizer).authorizeUpdate(eq(topicId), same(authenticatedUser));

            inOrder.verify(dtoValidator).validate(topicRequest);

            inOrder.verify(topicRepository).findById(topicId);

            inOrder.verifyNoMoreInteractions();

            verifyNoInteractions(
                    idempotencyTokenService,
                    topicMapper
            );
        }

        @Test
        void shouldThrowException_WhenIncorrectVersionDetectedComparingToFoundFromRepo() {
            // given
            doNothing().when(topicServiceAuthorizer).authorizeUpdate(any(), any());
            doNothing().when(dtoValidator).validate(any());

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicEntity topicEntity = topicBuilder
                    .withVersion((short) 133)
                    .build();
            UUID topicId = topicEntity.getId();
            short incorrectTopicVersion = 777;
            TopicRequest topicRequest = topicBuilder.buildRequest();

            when(topicRepository.findById(any())).thenReturn(Optional.of(topicEntity));

            // when
            assertThatThrownBy(() ->
                    topicServiceImpl.updateById(topicId, topicRequest, incorrectTopicVersion, authenticatedUser)
            ).isInstanceOfSatisfying(ServiceLayerException.class, ex ->

                    assertThat(ex.getExceptionMessage())
                            .isEqualTo(ExceptionMessage.OPERATION_FAILED__ENTITY_VERSION_DOES_NOT_MATCH)
            );

            // then

            InOrder inOrder = Mockito.inOrder(
                    topicServiceAuthorizer,
                    dtoValidator,
                    topicRepository
            );

            inOrder.verify(topicServiceAuthorizer).authorizeUpdate(eq(topicId), same(authenticatedUser));

            inOrder.verify(dtoValidator).validate(topicRequest);

            inOrder.verify(topicRepository).findById(topicId);

            inOrder.verifyNoMoreInteractions();

            verifyNoInteractions(
                    idempotencyTokenService,
                    topicMapper
            );
        }


        @Test
        void shouldUpdateWithMapper_On_CorrectVersion() {
            // given
            doNothing().when(topicServiceAuthorizer).authorizeUpdate(any(), any());
            doNothing().when(dtoValidator).validate(any());

            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            TopicTestDataBuilder topicBuilder = TopicTestDataBuilder.aTopic();
            TopicEntity topicEntity = topicBuilder.build();
            UUID topicId = topicEntity.getId();
            short topicVersion = topicEntity.getVersion();
            TopicRequest topicRequest = topicBuilder.buildRequest();

            doNothing().when(topicMapper).updateEntity(any(), any());
            when(topicRepository.findById(any())).thenReturn(Optional.of(topicEntity));


            // when
            topicServiceImpl.updateById(topicId, topicRequest, topicVersion, authenticatedUser);

            // then

            InOrder inOrder = Mockito.inOrder(
                    topicServiceAuthorizer,
                    dtoValidator,
                    topicRepository,
                    topicMapper
            );

            inOrder.verify(topicServiceAuthorizer).authorizeUpdate(eq(topicId), same(authenticatedUser));

            inOrder.verify(dtoValidator).validate(topicRequest);

            inOrder.verify(topicRepository).findById(topicId);

            inOrder.verify(topicMapper).updateEntity(topicEntity, topicRequest);

            inOrder.verifyNoMoreInteractions();

            verifyNoInteractions(
                    idempotencyTokenService
            );
        }
    }


    @Nested
    class deleteById_UUID_AuthenticatedUser {

        @Test
        void shouldDeleteById() {
            // given
            doNothing().when(topicServiceAuthorizer).authorizeDelete(any(), any());

            UUID idToDeleteBy = TopicTestDataBuilder.aTopic().getId();
            AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().build();

            doNothing().when(topicRepository).removeById(any());
            doNothing().when(idempotencyTokenService).deleteByCreationId(any());

            // when
            topicServiceImpl.deleteById(idToDeleteBy, authenticatedUser);

            // then
            InOrder inOrder = Mockito.inOrder(
                    topicServiceAuthorizer,
                    topicRepository,
                    idempotencyTokenService
            );

            inOrder.verify(topicServiceAuthorizer).authorizeDelete(eq(idToDeleteBy), same(authenticatedUser));

            inOrder.verify(topicRepository).removeById(idToDeleteBy);

            inOrder.verify(idempotencyTokenService).deleteByCreationId(CreationId.of(TopicEntity.class, idToDeleteBy));

            inOrder.verifyNoMoreInteractions();

            verifyNoInteractions(
                    dtoValidator,
                    topicMapper
            );
        }
    }
}
