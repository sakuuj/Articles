package by.sakuuj.blogsite.article;

import by.sakuuj.blogsite.article.dtos.TopicRequest;
import by.sakuuj.blogsite.article.dtos.TopicResponse;
import by.sakuuj.blogsite.article.entities.jpa.TopicEntity;
import by.sakuuj.blogsite.article.entities.jpa.embeddable.ModificationAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@With
@Getter
@AllArgsConstructor
@NoArgsConstructor(staticName = "aTopic")
public class TopicTestDataBuilder {

    private UUID id = UUID.fromString("3d525d62-c02c-47f6-b5f1-d13513bc6429");

    private String name = "Computer science";

    private ModificationAudit modificationAudit = ModificationAudit.builder()
            .createdAt(LocalDateTime.of(
                    LocalDate.of(2012, 5, 10),
                    LocalTime.of(12, 59, 10)
            ))
            .updatedAt(LocalDateTime.of(
                    LocalDate.of(2013, 6, 11),
                    LocalTime.of(9, 39, 11)
            ))
            .build();

    public TopicEntity build() {

        return TopicEntity.builder()
                .id(id)
                .name(name)
                .modificationAudit(modificationAudit)
                .build();
    }

    public TopicResponse buildResponse() {

        return TopicResponse.builder()
                .id(id)
                .name(name)
                .createdAt(modificationAudit.getCreatedAt())
                .updatedAt(modificationAudit.getUpdatedAt())
                .build();
    }

    public TopicRequest buildRequest() {
        return new TopicRequest(name);
    }
}
