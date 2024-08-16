package by.sakuuj.blogsite.article.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ArticleResponse(UUID id,
                              String title,
                              String content,
                              List<TopicResponse> topics,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              PersonResponse author
) {
}
