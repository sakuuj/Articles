package by.sakuuj.blogsite.entity.jpa.entities;

import by.sakuuj.blogsite.entity.jpa.CreationId;
import by.sakuuj.blogsite.entity.jpa.embeddable.IdempotencyTokenId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "idempotency_tokens")
public class IdempotencyTokenEntity implements Persistable<IdempotencyTokenId> {

    @EmbeddedId
    @Builder.Default
    private IdempotencyTokenId id = new IdempotencyTokenId();

    @Column(name = SqlAttributes.CREATION_ID)
    @Convert(converter = CreationId.Converter.class)
    private CreationId creationId;

    public static class SqlAttributes {
        public static final String CREATION_ID = "creation_id";
    }

    @Override
    public boolean isNew() {
        return true;
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

        if (!(o instanceof ArticleTopicEntity at)) {
            return false;
        }

        return getId() != null
                && getId().equals(at.getId());
    }
}
