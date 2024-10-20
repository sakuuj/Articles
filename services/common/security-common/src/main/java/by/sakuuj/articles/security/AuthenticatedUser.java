package by.sakuuj.articles.security;

import by.sakuuj.articles.person.grpc.Role;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AuthenticatedUser(
        UUID id,
        String primaryEmail,
        List<Role> roles,
        boolean isBlocked
) {
}
