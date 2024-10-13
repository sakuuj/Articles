package by.sakuuj.articles.article.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PersonResponse(
        UUID id,
        String primaryEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
