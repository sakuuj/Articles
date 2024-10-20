package by.sakuuj.articles.article.dto;

import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.articles.article.dto.validator.ValidArticleDocumentRequest;
import lombok.Builder;

@Builder
@ValidArticleDocumentRequest
public record ArticleDocumentRequest(RequestType type, ArticleDocument articleDocument) {

    public enum RequestType {
        UPSERT,
        DELETE
    }
}
