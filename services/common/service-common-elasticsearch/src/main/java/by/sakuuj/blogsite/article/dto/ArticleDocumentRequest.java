package by.sakuuj.blogsite.article.dto;

import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import jakarta.validation.constraints.NotNull;

public record ArticleDocumentRequest(@NotNull RequestType type,
                                     @NotNull ArticleDocument articleDocument) {

    public enum RequestType {
        UPSERT,
        DELETE
    }
}
