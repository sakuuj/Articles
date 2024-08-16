package by.sakuuj.blogsite.article.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponse(UUID id,
                              String content,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              PersonResponse author) {
}
