package by.sakuuj.articles.article.service.authorization;

import by.sakuuj.articles.security.AuthenticatedUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TopicServiceAuthorizerImpl implements TopicServiceAuthorizer {
    @Override
    public void authorizeCreate(AuthenticatedUser authenticatedUser) {

    }

    @Override
    public void authorizeUpdate(UUID topicId, AuthenticatedUser authenticatedUser) {

    }

    @Override
    public void authorizeDelete(UUID topicId, AuthenticatedUser authenticatedUser) {

    }
}
