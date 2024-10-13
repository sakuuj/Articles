package by.sakuuj.articles.security;

import by.sakuuj.articles.person.grpc.Email;
import by.sakuuj.articles.person.grpc.MaybePersonResponse;
import by.sakuuj.articles.person.grpc.PersonResponse;
import by.sakuuj.articles.person.grpc.Role;
import by.sakuuj.articles.person.grpc.SavePersonRequest;
import by.sakuuj.articles.service.PersonService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthenticatedUserAuthenticationFilter extends OncePerRequestFilter {

    private final PersonService personService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {

            filterChain.doFilter(request, response);
            return;
        }

        Jwt jwt = extractJwt(auth);
        String emailClaimValue = extractEmailClaimValue(jwt);

        AuthenticatedUser authenticatedUser = registerOrGetExistingUser(emailClaimValue);

        var authenticationToken = new AuthenticatedUserAuthenticationToken(authenticatedUser);

        replaceSecurityContext(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private static void replaceSecurityContext(AuthenticatedUserAuthenticationToken authenticationToken) {
        SecurityContext newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(authenticationToken);

        SecurityContextHolder.setContext(newContext);
    }

    private static String extractEmailClaimValue(Jwt jwt) {
        String emailClaimName = "email";
        if (!jwt.hasClaim(emailClaimName)) {
            throw new IllegalStateException("ID Token should contain 'email' claim");
        }
        return jwt.getClaim(emailClaimName);
    }

    private static Jwt extractJwt(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("Authentication should be of the type " + JwtAuthenticationToken.class);
        }

        Object principal = jwtAuth.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new IllegalStateException("Principal should be of the type " + Jwt.class);
        }
        return jwt;
    }

    private AuthenticatedUser registerOrGetExistingUser(String emailClaimValue) {

        Email emailRequest = Email.newBuilder()
                .setValue(emailClaimValue)
                .build();
        MaybePersonResponse maybePersonResponse = personService.getPersonByEmail(emailRequest);

        PersonResponse personResponse;
        if (maybePersonResponse.hasPersonResponse()) {
            personResponse = maybePersonResponse.getPersonResponse();
        } else {
            List<Role> defaultRoles = List.of(Role.USER);

            SavePersonRequest personToSave = SavePersonRequest.newBuilder()
                    .setPrimaryEmail(emailRequest)
                    .addAllRoles(defaultRoles)
                    .build();

            personResponse = personService.savePerson(personToSave);
        }

        return AuthenticatedUser.builder()
                .id(UUID.fromString(personResponse.getId().getValue()))
                .primaryEmail(personResponse.getPrimaryEmail().getValue())
                .roles(personResponse.getRolesList())
                .isBlocked(personResponse.getIsBlocked().getValue())
                .build();
    }
}
