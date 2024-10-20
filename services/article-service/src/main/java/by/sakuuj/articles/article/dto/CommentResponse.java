package by.sakuuj.articles.article.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentResponse(
        UUID id,
        String content,
        String createdAt,
        String updatedAt,
        PersonResponse author
) {
}
