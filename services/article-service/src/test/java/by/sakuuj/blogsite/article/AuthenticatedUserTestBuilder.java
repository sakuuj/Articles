package by.sakuuj.blogsite.article;

import by.sakuuj.blogsite.security.AuthenticatedUser;
import by.sakuuj.blogsite.person.grpc.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.UUID;

@With
@Getter
@AllArgsConstructor
@NoArgsConstructor(staticName = "newInstance")
public class AuthenticatedUserTestBuilder {

    private UUID id = UUID.fromString("84612ae1-ed0b-41b7-b20f-739ca0182d57");
    private String primaryEmail = "abobius228@gmail.com";
    private List<Role> roles = List.of(Role.USER);
    private boolean isBlocked = false;

    public AuthenticatedUser build() {

        return AuthenticatedUser.builder()
                .id(id)
                .roles(roles)
                .isBlocked(isBlocked)
                .primaryEmail(primaryEmail)
                .build();
    }
}
