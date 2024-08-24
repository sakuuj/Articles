package by.sakuuj.blogsite.article.service.authorization;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ArticleServiceAuthorizerImpl implements ArticleServiceAuthorizer {
    @Override
    public void authorizeCreate(UUID authorId, AuthenticatedUser authenticatedUser){
    }

    @Override
    public void authorizeDeleteById(UUID id, AuthenticatedUser authenticatedUser) {

    }

    @Override
    public void authorizeUpdateById(UUID id, AuthenticatedUser authenticatedUser) {

    }

    @Override
    public void authorizeAddTopic(UUID articleId, AuthenticatedUser authenticatedUser) {

    }

    @Override
    public void authorizeRemoveTopic(UUID articleId, AuthenticatedUser authenticatedUser) {

    }
}
