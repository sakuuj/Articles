package by.sakuuj.blogsite.article.service.authorization;

import lombok.Builder;

@Builder
public record AuthenticatedUser(String name) {
}
