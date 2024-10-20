package by.sakuuj.articles.article;

import by.sakuuj.articles.article.dto.TopicRequest;
import by.sakuuj.articles.article.dto.TopicResponse;
import by.sakuuj.articles.entity.jpa.entities.TopicEntity;
import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit;
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

    private String createdAtString = "2012-05-10T12:59:10";
    private String updatedAtString = "2013-06-11T09:39:11";

    private short version = 4322;

    public TopicEntity build() {

        return TopicEntity.builder()
                .id(id)
                .name(name)
                .version(version)
                .modificationAudit(modificationAudit)
                .build();
    }

    public TopicResponse buildResponse() {

        return TopicResponse.builder()
                .id(id)
                .name(name)
                .createdAt(createdAtString)
                .updatedAt(updatedAtString)
                .build();
    }

    public TopicRequest buildRequest() {
        return new TopicRequest(name);
    }
}
