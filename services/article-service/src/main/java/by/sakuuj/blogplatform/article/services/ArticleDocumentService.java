package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.controller.RequestedPage;
import by.sakuuj.blogplatform.article.entities.elasticsearch.ArticleDocument;
import by.sakuuj.blogplatform.article.repository.PageView;

import java.util.UUID;

public interface ArticleDocumentService {

    PageView<UUID> findSortedByRelevance(String searchTerms, RequestedPage requestedPage);

    PageView<UUID> findSortedByDatePublishedOnAndThenByRelevance(String searchTerms, RequestedPage requestedPage);

    UUID save(ArticleDocument articleDocument);

    void deleteById(UUID id);
}
