package by.sakuuj.blogplatform.article.services;

import by.sakuuj.blogplatform.article.dtos.ArticleDocumentResponse;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.mappers.ArticleDocumentMapper;
import by.sakuuj.blogplatform.article.repositories.PageView;
import by.sakuuj.blogplatform.article.repositories.elasticsearch.ArticleElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleDocumentServiceImpl implements ArticleDocumentService {

    private final ArticleDocumentMapper articleDocumentMapper;
    private final ArticleElasticsearchRepository articleElasticsearchRepository;


    @Override
    public PageView<ArticleDocumentResponse> findSortedByRelevance(String searchTerms, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return articleElasticsearchRepository
                .findSortedByRelevance(searchTerms, pageable)
                .map(articleDocumentMapper::toResponse);
    }

    @Override
    public PageView<ArticleDocumentResponse> findSortedByDatePublishedOnAndThenByRelevance(
            String searchTerms, int pageNumber, int pageSize
    ) {
        Sort sortByDatePublished = Sort
                .by(ArticleDocument.ElasticsearchFieldNames.DATE_PUBLISHED)
                .descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByDatePublished);

        return articleElasticsearchRepository
                .findSortedByRelevance(searchTerms, pageable)
                .map(articleDocumentMapper::toResponse);
    }

    @Override
    public ArticleDocumentResponse save(ArticleDocument articleDocument) {
        if (articleDocument.getId() == null) {
            throw new IllegalArgumentException("Can not save ArticleDocument with id set to null");
        }

        ArticleDocument saved = articleElasticsearchRepository.save(articleDocument);
        return articleDocumentMapper.toResponse(saved);
    }

    @Override
    public void deleteById(UUID id) {
        articleElasticsearchRepository.deleteById(id);
    }
}
