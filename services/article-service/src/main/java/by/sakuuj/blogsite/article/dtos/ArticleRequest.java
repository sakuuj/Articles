package by.sakuuj.blogsite.article.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ArticleRequest(@NotBlank @Size(max = 100)
                             String title,
                             @NotBlank @Size(max = 1_000_000)
                             String content
) {
}
