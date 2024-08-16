package by.sakuuj.blogplatform.article.dtos;

import lombok.Builder;

@Builder
public record CommentRequest(String content) {
}
