package by.sakuuj.blogsite.article.dto.validator;

import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidArticleDocumentRequestValidator
        implements ConstraintValidator<ValidArticleDocumentRequest, ArticleDocumentRequest> {

    @Override
    public boolean isValid(ArticleDocumentRequest value, ConstraintValidatorContext context) {

        ArticleDocumentRequest.RequestType requestType = value.type();
        ArticleDocument requestPayload = value.articleDocument();

        if (requestType == ArticleDocumentRequest.RequestType.DELETE) {
            return requestPayload == null;
        }

        if (requestType == ArticleDocumentRequest.RequestType.UPSERT) {
            return requestPayload != null;
        }

        return false;
    }
}
