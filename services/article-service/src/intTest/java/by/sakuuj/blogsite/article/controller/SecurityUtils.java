package by.sakuuj.blogsite.article.controller;

import by.sakuuj.blogsite.security.AuthenticatedUser;
import by.sakuuj.blogsite.security.AuthenticatedUserAuthenticationToken;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {

    public SecurityContext createSecurityContext(AuthenticatedUser authenticatedUser) {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new AuthenticatedUserAuthenticationToken(authenticatedUser));

        return securityContext;
    }
}
