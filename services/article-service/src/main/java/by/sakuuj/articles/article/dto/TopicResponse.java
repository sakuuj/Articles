package by.sakuuj.articles.article.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TopicResponse(
        UUID id,
        String name,
        String createdAt,
        String updatedAt
) {
}
