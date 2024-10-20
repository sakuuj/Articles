package by.sakuuj.articles.security;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class AuthenticatedUserAuthenticationToken implements Authentication {

    private final AuthenticatedUser authenticatedUser;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isAuthenticated;

    public AuthenticatedUserAuthenticationToken(AuthenticatedUser authenticatedUser) {

        Objects.requireNonNull(authenticatedUser);

        this.authenticatedUser = authenticatedUser;

        this.authorities = Optional.of(authenticatedUser.roles())
                .map(roles -> roles
                        .stream()
                        .map(r -> "ROLE_" + r.toString())
                        .map(SimpleGrantedAuthority::new)
                        .toList()
                ).orElse(List.of());

        this.isAuthenticated = !authenticatedUser.isBlocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return authenticatedUser.primaryEmail();
    }
}
