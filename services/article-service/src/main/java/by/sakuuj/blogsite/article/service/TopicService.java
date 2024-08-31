package by.sakuuj.blogsite.article.service;

import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.paging.PageView;

import java.util.Optional;
import java.util.UUID;

public interface TopicService {

    Optional<TopicResponse> findById(UUID id);

    PageView<TopicResponse> findAllSortByCreatedAtDesc();

    UUID create(TopicRequest request, UUID clientId, UUID idempotencyTokenValue);
    void deleteById(UUID id);
    void updateById(UUID id, TopicRequest newContent, short version);
}
