package by.sakuuj.blogplatform.article;

import by.sakuuj.blogplatform.article.dtos.ArticleRequest;
import by.sakuuj.blogplatform.article.dtos.ArticleResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.entities.ArticleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@With
@Getter
@AllArgsConstructor
@NoArgsConstructor(staticName = "anArticle")
public class ArticleTestDataBuilder {

    private UUID id = UUID.fromString("ce073d1b-fd75-4da4-9f1b-4e62edc822fc");

    private String title = "Java JIT Compilation";

    private String content = """
            Java HotSpot VM is a mixed-
            mode VM, which means that it
            starts off interpreting the byte-
            code, but it can (on a
            method-by-method
            basis) compile code
            into native machine
            instructions for faster
            execution.
            By passing the switch
            -XX:+PrintCompilation,
            you can see entries in
            the log file that show
            each method as it is
            compiled.
            """;

    private List<String> topics = List.of("Java", "Programming", "JIT");

    private LocalDateTime datePublishedOn = LocalDateTime.of(
            LocalDate.of(2012, 5, 10),
            LocalTime.of(12, 59, 10)
    );

    private LocalDateTime dateUpdatedOn = LocalDateTime.of(
            LocalDate.of(2012, 5, 10),
            LocalTime.of(12, 59, 10)
    );

    public ArticleResponse buildResponse() {
        return new ArticleResponse(id, title, content, topics, datePublishedOn, dateUpdatedOn);
    }

    public ArticleRequest buildRequest() {
        return new ArticleRequest(title, content, topics);
    }

    public ArticleEntity build() {
        return new ArticleEntity(id, title, content, datePublishedOn, dateUpdatedOn);
    }

    public ArticleDocument buildDocument() {
        return new ArticleDocument(id, title, content, datePublishedOn);
    }
}
