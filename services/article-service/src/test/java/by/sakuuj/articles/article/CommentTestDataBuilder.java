package by.sakuuj.articles.article;

import by.sakuuj.articles.article.dto.CommentRequest;
import by.sakuuj.articles.article.dto.CommentResponse;
import by.sakuuj.articles.article.dto.PersonResponse;
import by.sakuuj.articles.entity.jpa.embeddable.ModificationAudit;
import by.sakuuj.articles.entity.jpa.entities.ArticleEntity;
import by.sakuuj.articles.entity.jpa.entities.CommentEntity;
import by.sakuuj.articles.entity.jpa.entities.PersonEntity;
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
@NoArgsConstructor(staticName = "aComment")
public class CommentTestDataBuilder {

    private UUID id = UUID.fromString("d95b3c07-91c0-4443-aaa0-beffb98f452a");

    private String content = "This is my very clever comment. Very good article, keep it up!";

    private PersonTestDataBuilder personTestDataBuilder = PersonTestDataBuilder.aPerson();

    private PersonEntity author = personTestDataBuilder.build();
    private PersonResponse authorResponse = personTestDataBuilder.buildResponse();

    private ArticleEntity article = ArticleTestDataBuilder.anArticle().build();

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

    private short version = 32013;

    public CommentEntity build() {
        return CommentEntity.builder()
                .id(id)
                .author(author)
                .content(content)
                .article(article)
                .version(version)
                .modificationAudit(modificationAudit)
                .build();
    }

    public CommentRequest buildRequest() {
        return CommentRequest.builder()
                .content(content)
                .build();
    }

    public CommentResponse buildResponse() {
        return CommentResponse.builder()
                .id(id)
                .content(content)
                .author(authorResponse)
                .createdAt(createdAtString)
                .updatedAt(updatedAtString)
                .build();
    }
}
