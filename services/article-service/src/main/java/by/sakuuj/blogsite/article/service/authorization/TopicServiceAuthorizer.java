package by.sakuuj.blogsite.article.service.authorization;

import by.sakuuj.blogsite.security.AuthenticatedUser;

import java.util.UUID;

public interface TopicServiceAuthorizer {

    void authorizeCreate(AuthenticatedUser authenticatedUser);

    void authorizeUpdate(UUID topicId, AuthenticatedUser authenticatedUser);

    void authorizeDelete(UUID topicId, AuthenticatedUser authenticatedUser);
}
