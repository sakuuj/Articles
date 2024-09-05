package by.sakuuj.blogsite.security;

import by.sakuuj.blogsite.service.authorization.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

public record AuthenticatedUserAuthenticationToken (AuthenticatedUser authenticatedUser)
        implements Authentication {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authenticatedUser.roles()
                .stream()
                .map(r -> "ROLE_" + r.toString())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authenticatedUser;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // The token implies that user is already authenticated using jwt
    }

    @Override
    public String getName() {
        return authenticatedUser.primaryEmail();
    }
}
