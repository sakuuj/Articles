package by.sakuuj.articles.article.service.authorization;

import by.sakuuj.articles.security.AuthenticatedUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ArticleServiceAuthorizerImpl implements ArticleServiceAuthorizer {
    @Override
    public void authorizeCreate(AuthenticatedUser authenticatedUser) {

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
