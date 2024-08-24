package by.sakuuj.blogsite.article.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PersonRequest(@NotBlank @Email @Size(max = 50)
                            String primaryEmail
) {
}
