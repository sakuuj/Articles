package by.sakuuj.blogplatform.article.repositories.elasticsearch;

import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import by.sakuuj.blogplatform.article.repositories.PageView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResourceUtil;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleCustomElasticsearchRepositoryImpl implements ArticleCustomElasticsearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    private static final String FIND_MOST_RELEVANT_FOR_SEARCH_TERMS_QUERY = ResourceUtil
            .readFileFromClasspath("elasticsearchQueries/findMostRelevantForSearchTermsQuery.json");

    /**
     * <p>Find articles sorted by relevance using special query. </p>
     * <p>You may optionally provide an additional sort, if the {@link ArticleDocument} index supports such.
     * In that case, your additional sort will be applied before the sort, that orders by relevance.</p>
     *
     * @param searchTerms string of searching terms separated by spaces
     * @param initialPageable pageable containing searched page and optionally your custom sort
     * @return requested page sorted, initially, by your custom sort (if provided) and then by relevance.
     */
    @SuppressWarnings("unchecked")
    public PageView<ArticleDocument> findSortedByRelevance(String searchTerms, Pageable initialPageable) {

        String actualQueryContent = FIND_MOST_RELEVANT_FOR_SEARCH_TERMS_QUERY
                .replaceAll("\\?0", searchTerms);

        final Sort initialSort = initialPageable.getSort();
        Sort newSort = null;

        Sort sortByScore = Sort.by("_score").descending();
        if (initialSort.isUnsorted()) {
            newSort = sortByScore;
        } else {
            newSort = initialSort.and(sortByScore);
        }


        Pageable changedPageable = PageRequest.of(
                initialPageable.getPageNumber(),
                initialPageable.getPageSize(),
                newSort
        );

        StringQuery actualQuery =  StringQuery.builder(actualQueryContent)
                .withPageable(changedPageable)
                .withTrackTotalHits(false)
                .build();

        SearchHits<ArticleDocument> searchHits = elasticsearchOperations.search(actualQuery, ArticleDocument.class);
        SearchPage<ArticleDocument> searchedPage = SearchHitSupport.searchPageFor(searchHits, actualQuery.getPageable());

        List<ArticleDocument> content = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toCollection(ArrayList::new));

        return new PageView<>(
                content,
                searchedPage.getSize(),
                searchedPage.getNumber()
        );
    }
}
