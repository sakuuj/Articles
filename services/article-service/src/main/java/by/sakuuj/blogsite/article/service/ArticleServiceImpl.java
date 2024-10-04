package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.dtos.ArticleRequest;
import by.sakuuj.blogsite.article.dtos.ArticleResponse;
import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.validator.DtoValidator;
import by.sakuuj.blogsite.article.producer.ElasticsearchEventProducer;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.exception.ServiceLayerException;
import by.sakuuj.blogsite.article.exception.ServiceLayerExceptionMessage;
import by.sakuuj.blogsite.article.mapper.elasticsearch.ArticleDocumentMapper;
import by.sakuuj.blogsite.article.mapper.jpa.ArticleMapper;
import by.sakuuj.blogsite.article.repository.jpa.ArticleRepository;
import by.sakuuj.blogsite.article.repository.jpa.ArticleTopicRepository;
import by.sakuuj.blogsite.article.service.authorization.ArticleServiceAuthorizer;
import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.blogsite.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity;
import by.sakuuj.blogsite.entity.jpa.entities.ArticleEntity_;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleServiceAuthorizer articleServiceAuthorizer;

    private final DtoValidator dtoValidator;

    private final ArticleMapper articleMapper;
    private final ArticleDocumentMapper articleDocumentMapper;

    private final ArticleRepository articleRepository;
    private final ArticleTopicRepository articleTopicRepository;

    private final ElasticsearchEventProducer elasticsearchEventProducer;

    private final IdempotencyTokenService idempotencyTokenService;

    private final TransactionTemplate txTemplate;

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleResponse> findById(UUID id) {

        return articleRepository.findById(id)
                .map(articleMapper::toResponse);
    }

    @Override
    public PageView<ArticleResponse> findAllBySearchTermsSortedByRelevance(String searchTerms, RequestedPage requestedPage) {

        PageView<UUID> foundIds = articleDocumentRepository.findIdsOfDocsSortedByRelevance(
                searchTerms,
                PagingUtils.toPageable(requestedPage)
        );

        if (foundIds.content().isEmpty()) {
            return PageView.<ArticleResponse>empty().withNumberAndSize(requestedPage);
        }

        List<ArticleEntity> foundArticles = txTemplate.execute(
                txStatus -> articleRepository.findAllByIdsInOrder(foundIds.content())
        );

        PageView<ArticleEntity> foundPage = PageView.ofContent(foundArticles)
                .withSize(foundIds.size())
                .withNumber(foundIds.number());

        return foundPage.map(articleMapper::toResponse);
    }

    @Override
    @Transactional
    public PageView<ArticleResponse> findAllSortedByCreatedAtDesc(RequestedPage requestedPage) {

        Sort sortByCreatedAtDesc = Sort.by(
                Sort.Direction.DESC,
                ArticleEntity_.MODIFICATION_AUDIT + "." + ModificationAudit_.CREATED_AT
        );
        Pageable pageable = PagingUtils.toPageable(requestedPage, sortByCreatedAtDesc);

        Slice<ArticleResponse> foundArticles = articleRepository.findAll(pageable)
                .map(articleMapper::toResponse);

        return PagingUtils.toPageView(foundArticles);
    }

    @Override
    @Transactional
    public PageView<ArticleResponse> findAllByTopicsSortedByCreatedAtDesc(List<TopicRequest> topics, RequestedPage requestedPage) {

        List<String> topicsNames = topics.stream()
                .map(TopicRequest::name)
                .toList();

        return articleRepository.findAllByTopicsAndSortByCreatedAtDesc(topicsNames, requestedPage)
                .map(articleMapper::toResponse);
    }

    @Override
    @Transactional
    public UUID create(
            ArticleRequest request,
            UUID authorId,
            UUID idempotencyTokenValue,
            AuthenticatedUser authenticatedUser
    ) {
        articleServiceAuthorizer.authorizeCreate(authenticatedUser);

        dtoValidator.validate(request);

        IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                .clientId(authorId)
                .idempotencyTokenValue(idempotencyTokenValue)
                .build();

        idempotencyTokenService.findById(idempotencyTokenId)
                .ifPresent(token -> {
                    throw new ServiceLayerException(ServiceLayerExceptionMessage.CREATE_FAILED_IDEMPOTENCY_TOKEN_ALREADY_EXISTS);
                });

        ArticleEntity articleEntityToCreate = articleMapper.toEntity(request, authorId);
        articleRepository.save(articleEntityToCreate);

        UUID createdArticleId = articleEntityToCreate.getId();
        idempotencyTokenService.create(idempotencyTokenId, CreationId.of(ArticleEntity.class, createdArticleId));

        // TODO add transactionality
        ArticleDocument documentToSave = articleDocumentMapper.toDocument(articleEntityToCreate);
        articleDocumentRepository.save(documentToSave);

        return createdArticleId;
    }

    @Override
    @Transactional
    public void deleteById(UUID id, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeDeleteById(id, authenticatedUser);

        Optional<ArticleEntity> optionalArticleEntity = articleRepository.findById(id);
        if (optionalArticleEntity.isEmpty()) {
            return;
        }

        articleRepository.deleteById(id);
        idempotencyTokenService.deleteByCreationId(CreationId.of(ArticleEntity.class, id));

        // TODO add transactionality
        articleDocumentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateById(UUID id, ArticleRequest newContent, short version, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeUpdateById(id, authenticatedUser);

        dtoValidator.validate(newContent);

        ArticleEntity entityToUpdate = articleRepository.findById(id)
                .orElseThrow(() -> new ServiceLayerException(ServiceLayerExceptionMessage.UPDATE_FAILED_ENTITY_NOT_FOUND));

        if (entityToUpdate.getVersion() != version) {
            throw new ServiceLayerException(ServiceLayerExceptionMessage.OPERATION_FAILED_ENTITY_VERSION_DOES_NOT_MATCH);
        }

        articleMapper.updateEntity(entityToUpdate, newContent);

        // TODO add transactionality
        ArticleDocument updatedDocument = articleDocumentMapper.toDocument(entityToUpdate);
        articleDocumentRepository.save(updatedDocument);
    }

    @Override
    public void addTopic(UUID topicId, UUID articleId, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeAddTopic(articleId, authenticatedUser);

        ArticleTopicId articleTopicId = ArticleTopicId.builder()
                .articleId(articleId)
                .topicId(topicId)
                .build();

        articleTopicRepository.save(articleTopicId);
    }

    @Override
    public void removeTopic(UUID topicId, UUID articleId, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeAddTopic(articleId, authenticatedUser);

        ArticleTopicId articleTopicId = ArticleTopicId.builder()
                .articleId(articleId)
                .topicId(topicId)
                .build();

        articleTopicRepository.removeById(articleTopicId);
    }
}
