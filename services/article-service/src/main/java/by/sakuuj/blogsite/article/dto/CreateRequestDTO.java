package by.sakuuj.blogsite.article.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRequestDTO<T>(
        @NotNull UUID idempotencyTokenValue,
        @NotNull @Valid T payload
) {
}
