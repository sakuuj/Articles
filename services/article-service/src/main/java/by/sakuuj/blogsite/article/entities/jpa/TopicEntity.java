package by.sakuuj.blogsite.article.entities.jpa;

import by.sakuuj.blogsite.article.entities.jpa.embeddable.ModificationAudit;
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
import lombok.ToString;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "topics")
public class TopicEntity {

    @Id
    @Column(name = SqlAttributes.ID)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = SqlAttributes.NAME)
    private String name;

    @Embedded
    @Builder.Default
    private ModificationAudit modificationAudit = new ModificationAudit();

    @Version
    private short version;

    public static class SqlAttributes {
        public static final String ID = "topic_id";
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

        if (!(o instanceof TopicEntity t)) {
            return false;
        }

        return getId() != null
                && getId().equals(t.getId());
    }
}
