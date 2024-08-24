package by.sakuuj.blogsite.article.entity.jpa.entities;

import by.sakuuj.blogsite.article.entity.jpa.embeddable.ModificationAudit;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persons")
public class PersonEntity {

    @Id
    @Column(name = SqlAttributes.ID)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = SqlAttributes.PRIMARY_EMAIL)
    private String primaryEmail;

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @Version
    private short version;

    public static class SqlAttributes {
        public static final String ID = "person_id";
        public static final String PRIMARY_EMAIL = "primary_email";
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PersonEntity p)) {
            return false;
        }

        return getId() != null
                && getId().equals(p.getId());
    }
}
