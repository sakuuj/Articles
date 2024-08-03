package by.sakuuj.blogplatform.article.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ArticleDocumentResponse(UUID id,
                                      String title,
                                      String content,
                                      LocalDateTime datePublishedOn) {
}
