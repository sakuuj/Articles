package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repository.PageView;

import java.util.UUID;

public interface ArticleDocumentService {

    PageView<UUID> findSortedByRelevance(String searchTerms, int pageNumber, int pageSize);

    PageView<UUID> findSortedByDatePublishedOnAndThenByRelevance(String searchTerms, int pageNumber, int pageSize);

    UUID save(ArticleDocument articleDocument);

    void deleteById(UUID id);
}
