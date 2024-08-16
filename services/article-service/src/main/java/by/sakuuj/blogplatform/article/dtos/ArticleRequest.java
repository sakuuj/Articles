package by.sakuuj.blogplatform.article.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record ArticleRequest(String title,
                             String content,
                             List<TopicRequest> topics
) {
}
