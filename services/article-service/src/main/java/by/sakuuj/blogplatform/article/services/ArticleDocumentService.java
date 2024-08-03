package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.dtos.ArticleDocumentResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repositories.PageView;

import java.util.UUID;

public interface ArticleDocumentService {

    PageView<ArticleDocumentResponse> findSortedByRelevance(String searchTerms, int pageNumber, int pageSize);

    PageView<ArticleDocumentResponse> findSortedByDatePublishedOnAndThenByRelevance(String searchTerms, int pageNumber, int pageSize);

    ArticleDocumentResponse save(ArticleDocument articleDocument);

    void deleteById(UUID id);
}
