package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.paging.PageView;
import by.sakuuj.blogsite.paging.RequestedPage;
import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;

import java.util.Optional;
import java.util.UUID;

public interface TopicService {

    Optional<TopicResponse> findById(UUID id);

    PageView<TopicResponse> findAllSortByCreatedAtDesc(RequestedPage requestedPage);

    UUID create(TopicRequest request, UUID clientId, UUID idempotencyTokenValue, AuthenticatedUser authenticatedUser);

    void deleteById(UUID id, AuthenticatedUser authenticatedUser);

    void updateById(UUID id, TopicRequest newContent, short version, AuthenticatedUser authenticatedUser);
}
