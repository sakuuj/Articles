package by.sakuuj.blogsite.controller.resolvers;

import by.sakuuj.blogsite.security.AuthenticatedUserAuthenticationToken;
import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterType().equals(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AuthenticatedUserAuthenticationToken authenticatedUserAuthToken)) {
            throw new IllegalStateException(
                    String.format("Authentication should be of the type '%s'",
                            AuthenticatedUserAuthenticationToken.class)
            );
        }

        return authenticatedUserAuthToken.getAuthenticatedUser();
    }
}
