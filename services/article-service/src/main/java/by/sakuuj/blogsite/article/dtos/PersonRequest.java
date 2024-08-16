package by.sakuuj.blogsite.article.dtos;

import lombok.Builder;

@Builder
public record PersonRequest(String primaryEmail) {
}
