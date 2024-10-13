package by.sakuuj.articles.entity.jpa.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PersonToPersonRoleId {

    private UUID personId;
    private Short personRoleId;

    public static class SqlAttributes {

        public static final String PERSON_ID = "person_id";
        public static final String PERSON_ROLE_ID = "person_role_id";
    }
}
