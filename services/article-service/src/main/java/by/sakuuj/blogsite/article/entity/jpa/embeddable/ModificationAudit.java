package by.sakuuj.blogsite.article.entity.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ModificationAudit {

    @Column(name = SqlAttributes.UPDATED_AT)
    private LocalDateTime updatedAt;

    @Column(name = SqlAttributes.CREATED_AT, updatable = false)
    private LocalDateTime createdAt;

    public static class SqlAttributes {
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
    }

    @PrePersist
    private void beforePublish() {
        LocalDateTime currentTimeUTC = LocalDateTime.now(Clock.systemUTC());

        createdAt = currentTimeUTC;
        updatedAt = currentTimeUTC;
    }

    @PreUpdate
    private void beforeUpdate() {
        LocalDateTime currentTimeUTC = LocalDateTime.now(Clock.systemUTC());

        updatedAt = currentTimeUTC;
    }
}
