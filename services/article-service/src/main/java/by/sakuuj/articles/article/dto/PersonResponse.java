package by.sakuuj.articles.article.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PersonResponse(
        UUID id,
        String primaryEmail,
        String createdAt,
        String updatedAt
) {
}
