package by.sakuuj.articles.article.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdateRequestDTO<T>(
        short version,
        @NotNull @Valid T payload
) {
}
