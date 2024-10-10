package by.sakuuj.blogsite.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentRequest(
        @NotBlank @Size(max = 10_000)
        String content
) {
}
