package by.sakuuj.articles.article.service.authorization;

import by.sakuuj.articles.security.AuthenticatedUser;

import java.util.UUID;

public interface ArticleServiceAuthorizer {

    void authorizeCreate(AuthenticatedUser authenticatedUser);

    void authorizeDeleteById(UUID articleId, AuthenticatedUser authenticatedUser);
    void authorizeUpdateById(UUID articleId, AuthenticatedUser authenticatedUser);

    void authorizeAddTopic(UUID articleId, AuthenticatedUser authenticatedUser);
    void authorizeRemoveTopic(UUID articleId, AuthenticatedUser authenticatedUser);

}
