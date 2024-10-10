package by.sakuuj.blogsite.article.dto;

import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.dto.validator.ValidArticleDocumentRequest;
import lombok.Builder;

@Builder
@ValidArticleDocumentRequest
public record ArticleDocumentRequest(RequestType type, ArticleDocument articleDocument) {

    public enum RequestType {
        UPSERT,
        DELETE
    }
}
