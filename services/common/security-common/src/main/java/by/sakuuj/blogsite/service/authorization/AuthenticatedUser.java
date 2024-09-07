package by.sakuuj.blogsite.service.authorization;

import by.sakuuj.blogsite.person.grpc.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record AuthenticatedUser(
        String primaryEmail,
        List<Role> roles,
        boolean isBlocked
) {
}
