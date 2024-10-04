package by.sakuuj.blogsite.entity.jpa.entities;

import by.sakuuj.blogsite.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId;
import by.sakuuj.blogsite.entity.jpa.embeddable.PersonToPersonRoleId_;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person_to_person_role")
public class PersonToPersonRoleEntity {

    @EmbeddedId
    @Builder.Default
    private PersonToPersonRoleId id = new PersonToPersonRoleId();

    @MapsId(PersonToPersonRoleId_.PERSON_ID)
    @JoinColumn(name = PersonToPersonRoleId.SqlAttributes.PERSON_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonEntity person;

    @MapsId(PersonToPersonRoleId_.PERSON_ROLE_ID)
    @JoinColumn(name = PersonToPersonRoleId.SqlAttributes.PERSON_ROLE_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PersonRoleEntity personRole;

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @Version
    private short version;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PersonToPersonRoleEntity ptpr)) {
            return false;
        }

        return getId() != null
                && getId().equals(ptpr.getId());
    }
}
