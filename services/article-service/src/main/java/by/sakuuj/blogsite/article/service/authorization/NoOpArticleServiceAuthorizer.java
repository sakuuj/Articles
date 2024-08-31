package by.sakuuj.blogsite.article.service.authorization;

import java.util.UUID;

public class NoOpArticleServiceAuthorizer implements ArticleServiceAuthorizer {

    @Override
    public void authorizeCreate(UUID authorId, AuthenticatedUser authenticatedUser) {
    }

    @Override
    public void authorizeDeleteById(UUID articleId, AuthenticatedUser authenticatedUser) {
    }

    @Override
    public void authorizeUpdateById(UUID articleId, AuthenticatedUser authenticatedUser) {
    }

    @Override
    public void authorizeAddTopic(UUID articleId, AuthenticatedUser authenticatedUser) {
    }

    @Override
    public void authorizeRemoveTopic(UUID articleId, AuthenticatedUser authenticatedUser) {
    }
}
