package by.sakuuj.articles.article.repository.elasticsearch.custom;

import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.articles.paging.PageView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArticleDocumentCustomRepositoryImpl implements ArticleDocumentCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final QueryProvider queryProvider;
    private final SearchHitsToPageViewMapper searchHitsToPageViewMapper;

    /**
     * <p>Find articles sorted by relevance using special query. </p>
     * <p>You may optionally provide an additional sort, if the {@link ArticleDocument} index supports such.
     * In that case, your additional sort will be applied before the sort, that orders by relevance.</p>
     *
     * @param searchTerms     string of searching terms separated by spaces
     * @param pageable pageable containing searched page and optionally your custom sort
     * @return requested page sorted, initially, by your custom sort (if provided) and then by relevance.
     */
    public PageView<UUID> findIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable) {

        StringQuery query = queryProvider.provideQueryToFindIdsOfDocsSortedByRelevance(searchTerms, pageable);

        SearchHits<ArticleDocument> searchHits = elasticsearchOperations.search(query, ArticleDocument.class);

        return searchHitsToPageViewMapper.map(searchHits, query.getPageable(), ArticleDocument::getId);
    }
}
