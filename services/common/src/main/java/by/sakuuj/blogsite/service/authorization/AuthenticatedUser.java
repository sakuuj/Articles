package by.sakuuj.blogsite.service.authorization;

import lombok.Builder;

import java.util.List;

@Builder
public record AuthenticatedUser(String primaryEmail, List<Role> roles) {
}
