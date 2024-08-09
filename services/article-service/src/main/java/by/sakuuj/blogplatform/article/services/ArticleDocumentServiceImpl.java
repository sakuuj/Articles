package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repository.PageView;
import by.sakuuj.blogplatform.article.repository.elasticsearch.ArticleDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleDocumentServiceImpl implements ArticleDocumentService {

    private final ArticleDocumentRepository articleDocumentRepository;

    @Override
    public PageView<UUID> findSortedByRelevance(String searchTerms, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return articleDocumentRepository
                .findIdsOfDocsSortedByRelevance(searchTerms, pageable);
    }

    @Override
    public PageView<UUID> findSortedByDatePublishedOnAndThenByRelevance(
            String searchTerms, int pageNumber, int pageSize
    ) {
        Sort sortByDatePublished = Sort
                .by(ArticleDocument.ElasticsearchFieldNames.DATE_PUBLISHED)
                .descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByDatePublished);

        return articleDocumentRepository
                .findIdsOfDocsSortedByRelevance(searchTerms, pageable);
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
