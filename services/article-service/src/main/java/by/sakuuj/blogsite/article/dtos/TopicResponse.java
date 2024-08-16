package by.sakuuj.blogsite.article.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TopicResponse(UUID id,
                            String name,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt
) {
}
