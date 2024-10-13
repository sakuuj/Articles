package by.sakuuj.articles.article.service;

import by.sakuuj.articles.article.dto.ArticleRequest;
import by.sakuuj.articles.article.dto.ArticleResponse;
import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.security.AuthenticatedUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleService {

    Optional<ArticleResponse> findById(UUID id);

    PageView<ArticleResponse> findAllBySearchTermsSortedByRelevance(String searchTerms, RequestedPage requestedPage);

    PageView<ArticleResponse> findAllSortedByCreatedAtDesc(RequestedPage requestedPage);

    PageView<ArticleResponse> findAllByTopicsSortedByCreatedAtDesc(List<TopicRequest> topics, RequestedPage requestedPage);

    UUID create(ArticleRequest request, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser);

    void deleteById(UUID id, AuthenticatedUser authenticatedUser);
    void updateById(UUID id, ArticleRequest newContent, short version, AuthenticatedUser authenticatedUser);

    void addTopic(UUID topicId, UUID articleId, AuthenticatedUser authenticatedUser);
    void removeTopic(UUID topicId, UUID articleId, AuthenticatedUser authenticatedUser);
}
