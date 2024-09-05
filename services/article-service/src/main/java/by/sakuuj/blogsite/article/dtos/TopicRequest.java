package by.sakuuj.blogsite.article.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TopicRequest(
        @NotBlank @Size(max = 50)
        String name
) {
}
