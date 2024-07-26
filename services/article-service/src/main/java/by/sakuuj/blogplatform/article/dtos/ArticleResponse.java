package by.sakuuj.blogplatform.article.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ArticleResponse(UUID id,
                              String title,
                              char[] content,
                              List<String> topics,
                              LocalDateTime datePublishedOn,
                              LocalDateTime dateUpdatedOn
) {
}
