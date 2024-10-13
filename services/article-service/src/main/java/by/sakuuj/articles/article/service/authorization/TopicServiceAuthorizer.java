package by.sakuuj.articles.article.service.authorization;

import by.sakuuj.articles.security.AuthenticatedUser;

import java.util.UUID;

public interface TopicServiceAuthorizer {

    void authorizeCreate(AuthenticatedUser authenticatedUser);

    void authorizeUpdate(UUID topicId, AuthenticatedUser authenticatedUser);

    void authorizeDelete(UUID topicId, AuthenticatedUser authenticatedUser);
}
