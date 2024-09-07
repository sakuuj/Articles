package by.sakuuj.blogsite.entity.jpa.entities;

import by.sakuuj.blogsite.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.blogsite.entity.jpa.utils.EntityGraphNames;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persons")
@NamedEntityGraph(name = EntityGraphNames.PERSON_EAGER_WITH_PERSON_ROLES_EAGER,
        attributeNodes = {
                @NamedAttributeNode(value = PersonEntity_.PERSON_TO_PERSON_ROLE_LIST, subgraph = "person_role_list_eager")
        },
        subgraphs = {
                @NamedSubgraph(name = "person_role_list_eager", attributeNodes = {
                        @NamedAttributeNode(PersonToPersonRoleEntity_.PERSON),
                        @NamedAttributeNode(PersonToPersonRoleEntity_.PERSON_ROLE)
                })
        }
)
public class PersonEntity {

    @Id
    @Column(name = SqlAttributes.ID)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = SqlAttributes.PRIMARY_EMAIL)
    private String primaryEmail;

    @Builder.Default
    @OneToMany(mappedBy = PersonToPersonRoleEntity_.PERSON)
    private List<PersonToPersonRoleEntity> personToPersonRoleList = new ArrayList<>();

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @Builder.Default
    @Column(name = SqlAttributes.IS_BLOCKED)
    private boolean isBlocked = false;

    @Version
    private short version;

    public static class SqlAttributes {
        public static final String ID = "person_id";
        public static final String PRIMARY_EMAIL = "primary_email";
        public static final String IS_BLOCKED = "is_blocked";
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
