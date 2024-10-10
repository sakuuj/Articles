package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.dto.ArticleRequest;
import by.sakuuj.blogsite.article.dto.ArticleResponse;
import by.sakuuj.blogsite.article.dto.TopicRequest;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleService {

    Optional<ArticleResponse> findById(UUID id);

    PageView<ArticleResponse> findAllBySearchTermsSortedByRelevance(String searchTerms, RequestedPage requestedPage);

    PageView<ArticleResponse> findAllSortedByCreatedAtDesc(RequestedPage requestedPage);

    PageView<ArticleResponse> findAllByTopicsSortedByCreatedAtDesc(List<TopicRequest> topics, RequestedPage requestedPage);

    UUID create(ArticleRequest request, UUID authorId, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser);

    void deleteById(UUID id, AuthenticatedUser authenticatedUser);
    void updateById(UUID id, ArticleRequest newContent, short version, AuthenticatedUser authenticatedUser);

    void addTopic(UUID topicId, UUID articleId, AuthenticatedUser authenticatedUser);
    void removeTopic(UUID topicId, UUID articleId, AuthenticatedUser authenticatedUser);
}
