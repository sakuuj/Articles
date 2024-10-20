package by.sakuuj.articles.article.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ArticleResponse(
        UUID id,
        String title,
        String content,
        List<TopicResponse> topics,
        String createdAt,
        String updatedAt,
        PersonResponse author
) {
}
