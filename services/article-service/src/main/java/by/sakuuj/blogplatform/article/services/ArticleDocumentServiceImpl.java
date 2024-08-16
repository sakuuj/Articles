package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.controller.RequestedPage;
import by.sakuuj.blogplatform.article.entities.elasticsearch.ArticleDocument;
import by.sakuuj.blogplatform.article.repository.PageView;
import by.sakuuj.blogplatform.article.repository.elasticsearch.ArticleDocumentRepository;
import by.sakuuj.blogplatform.article.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleDocumentServiceImpl implements ArticleDocumentService {

    private final ArticleDocumentRepository articleDocumentRepository;

    @Override
    public PageView<UUID> findSortedByRelevance(String searchTerms, RequestedPage requestedPage) {

        Pageable pageable = PagingUtils.toPageable(requestedPage);
        return articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerms, pageable);
    }

    @Override
    public PageView<UUID> findSortedByDatePublishedOnAndThenByRelevance(String searchTerms, RequestedPage requestedPage) {

        Pageable pageable = PagingUtils.toPageable(requestedPage);

        Pageable pageableSortedByDatePublished = PagingUtils.addDescSort(
                pageable,
                ArticleDocument.ElasticsearchFieldNames.DATE_PUBLISHED
        );

        return articleDocumentRepository.findIdsOfDocsSortedByRelevance(searchTerms, pageableSortedByDatePublished);
    }

    @Override
    public UUID save(ArticleDocument articleDocument) {
        if (articleDocument.getId() == null) {
            throw new IllegalArgumentException("Can not save ArticleDocument with id set to null");
        }

        return articleDocumentRepository.save(articleDocument).getId();
    }

    @Override
    public void deleteById(UUID id) {
        articleDocumentRepository.deleteById(id);
    }
}
