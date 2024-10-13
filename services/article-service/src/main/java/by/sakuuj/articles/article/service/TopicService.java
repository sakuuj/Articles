package by.sakuuj.articles.article.service;

import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.TopicResponse;
import by.sakuuj.articles.paging.PageView;
import by.sakuuj.articles.paging.RequestedPage;
import by.sakuuj.articles.security.AuthenticatedUser;

import java.util.Optional;
import java.util.UUID;

public interface TopicService {

    Optional<TopicResponse> findById(UUID id);

    PageView<TopicResponse> findAllSortByCreatedAtDesc(RequestedPage requestedPage);

    UUID create(TopicRequest request, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser);

    void deleteById(UUID id, AuthenticatedUser authenticatedUser);

    void updateById(UUID id, TopicRequest newContent, short version, AuthenticatedUser authenticatedUser);
}
