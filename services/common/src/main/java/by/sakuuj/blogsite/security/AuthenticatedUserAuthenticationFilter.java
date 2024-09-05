package by.sakuuj.blogsite.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticatedUserAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {

            filterChain.doFilter(request, response);
            return;
        }

        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("Authentication should be of the type " + JwtAuthenticationToken.class);
        }

        Object principal = jwtAuth.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new IllegalStateException("Principal should be of the type " + Jwt.class);
        }

        String emailClaimName = "email";
        if (!jwt.hasClaim(emailClaimName)) {
            throw new IllegalStateException("ID Token should contain 'email' claim");
        }

        String emailClaimValue = jwt.getClaim(emailClaimName);

    }
}
