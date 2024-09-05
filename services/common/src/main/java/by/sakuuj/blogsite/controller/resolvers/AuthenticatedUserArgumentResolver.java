package by.sakuuj.blogsite.controller.resolvers;

import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("Authentication should be of the type " + JwtAuthenticationToken.class);
        }

        Object principal = jwtAuth.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new IllegalStateException("Principal should be of the type " + Jwt.class);
        }

        return mapJwt(jwt);
    }

    private static AuthenticatedUser mapJwt(Jwt jwt) {

        String emailClaimName = "email";
        if (!jwt.hasClaim(emailClaimName)) {
            throw new IllegalStateException("ID Token should contain 'email' claim");
        }

        String emailClaimValue = jwt.getClaim(emailClaimName);

        return AuthenticatedUser.builder()
                .primaryEmail(emailClaimValue)
                .build();
    }
}
