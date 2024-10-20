package by.sakuuj.articles.entity.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import liquibase.sql.Sql;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person_roles")
public class PersonRoleEntity {

    @Id
    @Column(name = SqlAttributes.ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @NaturalId
    @Column(name = SqlAttributes.NAME)
    private String name;

    @Version
    private short version;

    public static class SqlAttributes {
        public static final String ID = "person_role_id";
        public static final String NAME = "name";
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

        if (!(o instanceof PersonRoleEntity pr)) {
            return false;
        }

        return getId() != null
                && getId().equals(pr.getId());
    }
}
