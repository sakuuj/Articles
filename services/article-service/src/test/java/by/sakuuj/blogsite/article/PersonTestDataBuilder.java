package by.sakuuj.blogsite.article;

import by.sakuuj.blogsite.article.dtos.PersonRequest;
import by.sakuuj.blogsite.article.dtos.PersonResponse;
import by.sakuuj.blogsite.entity.jpa.entities.PersonEntity;
import by.sakuuj.blogsite.entity.jpa.embeddable.ModificationAudit;
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
@NoArgsConstructor(staticName = "aPerson")
public class PersonTestDataBuilder {

    private UUID id = UUID.fromString("3f774bbd-c9ee-42ca-9fa3-40310d8a80d5");

    private String primaryEmail = "my_primary_email@gmail.com";

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

    private short version = 4329;


    public PersonEntity build() {

        return PersonEntity.builder()
                .id(id)
                .version(version)
                .primaryEmail(primaryEmail)
                .modificationAudit(modificationAudit)
                .build();
    }

    public PersonResponse buildResponse() {

        return PersonResponse.builder()
                .id(id)
                .primaryEmail(primaryEmail)
                .createdAt(modificationAudit.getCreatedAt())
                .updatedAt(modificationAudit.getUpdatedAt())
                .build();
    }

    public PersonRequest buildRequest() {
        return new PersonRequest(primaryEmail);
    }
}
