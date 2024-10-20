package by.sakuuj.articles.article.service;

import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.TopicResponse;
import by.sakuuj.articles.article.dto.validator.DtoValidator;
import by.sakuuj.articles.article.exception.EntityNotFoundException;
import by.sakuuj.articles.article.exception.IdempotencyTokenExistsException;
import by.sakuuj.articles.article.exception.EntityVersionDoesNotMatch;
import by.sakuuj.articles.article.mapper.jpa.TopicMapper;
import by.sakuuj.articles.article.repository.jpa.TopicRepository;
import by.sakuuj.articles.article.service.authorization.TopicServiceAuthorizer;
import by.sakuuj.articles.entity.jpa.CreationId;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity_;
import by.sakuuj.articles.entity.jpa.entities.IdempotencyTokenEntity;
import by.sakuuj.articles.entity.jpa.entities.TopicEntity;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.service.IdempotencyTokenService;
import by.sakuuj.articles.security.AuthenticatedUser;
import by.sakuuj.articles.utils.PagingUtils;
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
    public UUID create(TopicRequest request, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser) {

        topicServiceAuthorizer.authorizeCreate(authenticatedUser);
        dtoValidator.validate(request);

        IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                .idempotencyTokenValue(idempotencyTokenValue)
                .clientId(authenticatedUser.id())
                .build();

        Optional<IdempotencyTokenEntity> foundToken = idempotencyTokenService.findById(idempotencyTokenId);
        if (foundToken.isPresent()) {
            throw new IdempotencyTokenExistsException();
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

        TopicEntity topicToUpdate = topicRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (topicToUpdate.getVersion() != version) {
            throw new EntityVersionDoesNotMatch();
        }

        topicMapper.updateEntity(topicToUpdate, newContent);
    }
}
