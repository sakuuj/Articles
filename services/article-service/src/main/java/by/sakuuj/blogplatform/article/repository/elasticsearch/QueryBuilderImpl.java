package by.sakuuj.blogplatform.article.repository.elasticsearch;

import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ResourceUtil;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderImpl implements QueryBuilder {

    public static final String FIND_MOST_RELEVANT_QUERY_JSON_PATH = "elasticsearchQueries/findMostRelevantForSearchTermsQuery.json";
    private static final String FIND_MOST_RELEVANT_QUERY = ResourceUtil.readFileFromClasspath(
            FIND_MOST_RELEVANT_QUERY_JSON_PATH
    );

    private static final SourceFilter excludeAllSourceFilter = FetchSourceFilter.of(
            new String[]{},
            new String[]{"*"}
    );

    @Override
    public StringQuery buildQueryToFindIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable) {
        String actualQueryContent = FIND_MOST_RELEVANT_QUERY.replaceAll("\\?0", searchTerms);

        Pageable finalPageable = addSortByScore(pageable);

        return StringQuery.builder(actualQueryContent)
                .withPageable(finalPageable)
                .withFields(ArticleDocument.ElasticsearchFieldNames.ID)
                .withSourceFilter(excludeAllSourceFilter)
                .withTrackTotalHits(false)
                .build();
    }

    private static Pageable addSortByScore(Pageable initialPageable) {

        final Sort initialSort = initialPageable.getSort();
        Sort newSort = null;

        final Sort sortByScore = Sort.by("_score").descending();
        if (initialSort.isUnsorted()) {
            newSort = sortByScore;
        } else {
            newSort = initialSort.and(sortByScore);
        }

        return PageRequest.of(
                initialPageable.getPageNumber(),
                initialPageable.getPageSize(),
                newSort
        );
    }
}
