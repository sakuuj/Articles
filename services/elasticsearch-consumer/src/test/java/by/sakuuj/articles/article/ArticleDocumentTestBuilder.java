package by.sakuuj.articles.article;

import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Getter
@AllArgsConstructor
@NoArgsConstructor(staticName = "anArticleDocument")
public class ArticleDocumentTestBuilder {

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

    private LocalDateTime datePublishedOn = LocalDateTime.of(2021, 10, 17, 11, 11, 11);

    public ArticleDocument buildDocument() {
        return new ArticleDocument(id, title, content, datePublishedOn);
    }
}
