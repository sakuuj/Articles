package by.sakuuj.articles.article.service;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.validator.DtoValidator;
import by.sakuuj.articles.article.mapper.jpa.ArticleMapper;
import by.sakuuj.articles.article.repository.elasticsearch.ArticleDocumentRepository;
import by.sakuuj.articles.article.repository.jpa.ArticleRepository;
import by.sakuuj.articles.article.repository.jpa.ArticleTopicRepository;
import by.sakuuj.articles.article.service.authorization.ArticleServiceAuthorizer;
import by.sakuuj.articles.article.orchestration.OrchestratedArticleService;
import by.sakuuj.articles.entity.jpa.embeddable.ArticleTopicId;
import by.sakuuj.articles.entity.jpa.embeddable.IdempotencyTokenId;
import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit_;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity_;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.security.AuthenticatedUser;
import by.sakuuj.articles.utils.PagingUtils;
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

    private final ArticleRepository articleRepository;
    private final ArticleDocumentRepository articleDocumentRepository;
    private final ArticleTopicRepository articleTopicRepository;

    private final OrchestratedArticleService orchestratedArticleService;

    private final TransactionTemplate txTemplate;

    @Override
    public UUID create(ArticleRequest request, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeCreate(authenticatedUser);

        dtoValidator.validate(request);

        IdempotencyTokenId idempotencyTokenId = IdempotencyTokenId.builder()
                .clientId(authenticatedUser.id())
                .idempotencyTokenValue(idempotencyTokenValue)
                .build();

        ArticleResponse createdArticle = orchestratedArticleService.create(request, idempotencyTokenId);

        return createdArticle.id();
    }

    @Override
    public void deleteById(UUID id, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeDeleteById(id, authenticatedUser);

        orchestratedArticleService.deleteById(id);
    }

    @Override
    public void updateById(UUID id, ArticleRequest request, short version, AuthenticatedUser authenticatedUser) {

        articleServiceAuthorizer.authorizeUpdateById(id, authenticatedUser);

        dtoValidator.validate(request);

        orchestratedArticleService.updateById(request, id, version);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleResponse> findById(UUID id) {

        return articleRepository.findById(id).map(articleMapper::toResponse);
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
