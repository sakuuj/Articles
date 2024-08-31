package by.sakuuj.blogsite.article.service.authorization;

import java.util.UUID;

public interface ArticleServiceAuthorizer {

    void authorizeCreate(AuthenticatedUser authenticatedUser);

    void authorizeDeleteById(UUID articleId, AuthenticatedUser authenticatedUser);
    void authorizeUpdateById(UUID articleId, AuthenticatedUser authenticatedUser);

    void authorizeAddTopic(UUID articleId, AuthenticatedUser authenticatedUser);
    void authorizeRemoveTopic(UUID articleId, AuthenticatedUser authenticatedUser);

}
