package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.dtos.validator.DtoValidator;
import by.sakuuj.blogsite.article.exception.ServiceLayerException;
import by.sakuuj.blogsite.article.exception.ServiceLayerExceptionMessage;
import by.sakuuj.blogsite.article.mapper.jpa.TopicMapper;
import by.sakuuj.blogsite.article.repository.jpa.TopicRepository;
import by.sakuuj.blogsite.article.service.authorization.TopicServiceAuthorizer;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity_;
import by.sakuuj.blogsite.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.blogsite.entity.jpa.entities.TopicEntity;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import by.sakuuj.blogsite.service.IdempotencyTokenService;
import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;
import by.sakuuj.blogsite.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicServiceAuthorizer topicServiceAuthorizer;
    private final DtoValidator dtoValidator;
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    private final IdempotencyTokenService idempotencyTokenService;


    @Override
    @Transactional(readOnly = true)
    public Optional<TopicResponse> findById(UUID id) {
        return topicRepository.findById(id).map(topicMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageView<TopicResponse> findAllSortByCreatedAtDesc(RequestedPage requestedPage) {

        Sort sortByCreatedAtDesc = Sort.by(
                Sort.Direction.DESC,
                ArticleEntity_.MODIFICATION_AUDIT + "." + ModificationAudit_.CREATED_AT
        );

        Pageable pageable = PagingUtils.toPageable(requestedPage, sortByCreatedAtDesc);
        Slice<TopicEntity> found = topicRepository.findAll(pageable);

        return PagingUtils.toPageView(found).map(topicMapper::toResponse);
    }

    @Override
    public UUID create(TopicRequest request, UUID clientId, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser) {

        topicServiceAuthorizer.authorizeCreate(authenticatedUser);
        dtoValidator.validate(request);

        IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                .idempotencyTokenValue(idempotencyTokenValue)
                .clientId(clientId)
                .build();

        Optional<IdempotencyTokenEntity> foundToken = idempotencyTokenService.findById(idempotencyTokenId);
        if (foundToken.isPresent()) {
            throw new ServiceLayerException(ServiceLayerExceptionMessage.CREATE_FAILED__IDEMPOTENCY_TOKEN_ALREADY_EXISTS);
        }

        TopicEntity topicEntityToSave = topicMapper.toEntity(request);
        topicRepository.save(topicEntityToSave);

        UUID savedEntityId = topicEntityToSave.getId();

        CreationId creationId = CreationId.of(TopicEntity.class, savedEntityId);
        idempotencyTokenService.create(idempotencyTokenId, creationId);

        return savedEntityId;
    }

    @Override
    public void deleteById(UUID id, AuthenticatedUser authenticatedUser) {

        topicServiceAuthorizer.authorizeDelete(id, authenticatedUser);
        topicRepository.removeById(id);

        CreationId creationId = CreationId.of(TopicEntity.class, id);
        idempotencyTokenService.deleteByCreationId(creationId);
    }

    @Override
    public void updateById(UUID id, TopicRequest newContent, short version, AuthenticatedUser authenticatedUser) {

        topicServiceAuthorizer.authorizeUpdate(id, authenticatedUser);
        dtoValidator.validate(newContent);

        TopicEntity topicToUpdate = topicRepository.findById(id).orElseThrow(() ->
                new ServiceLayerException(ServiceLayerExceptionMessage.UPDATE_FAILED__ENTITY_NOT_FOUND)
        );

        if (topicToUpdate.getVersion() != version) {
            throw new ServiceLayerException(ServiceLayerExceptionMessage.OPERATION_FAILED__ENTITY_VERSION_DOES_NOT_MATCH);
        }

        topicMapper.updateEntity(topicToUpdate, newContent);
    }
}
