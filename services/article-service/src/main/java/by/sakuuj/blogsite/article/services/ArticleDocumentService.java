package by.sakuuj.blogsite.article.services;

import by.sakuuj.blogsite.article.controller.RequestedPage;
import by.sakuuj.blogsite.article.entities.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.repository.PageView;

import java.util.UUID;

public interface ArticleDocumentService {

    PageView<UUID> findSortedByRelevance(String searchTerms, RequestedPage requestedPage);

    PageView<UUID> findSortedByDatePublishedOnAndThenByRelevance(String searchTerms, RequestedPage requestedPage);

    UUID save(ArticleDocument articleDocument);

    void deleteById(UUID id);
}
