package by.sakuuj.blogsite.article.repository.elasticsearch;

import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.utils.PagingUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ResourceUtil;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Component;

@Component
public class QueryProviderImpl implements QueryProvider {

    public static final String FIND_MOST_RELEVANT_QUERY_JSON_PATH = "elasticsearchQueries/findMostRelevantForSearchTermsQuery.json";
    private static final String FIND_MOST_RELEVANT_QUERY = ResourceUtil.readFileFromClasspath(
            FIND_MOST_RELEVANT_QUERY_JSON_PATH
    );

    private static final SourceFilter excludeAllSourceFilter = FetchSourceFilter.of(
            new String[]{},
            new String[]{"*"}
    );

    @Override
    public StringQuery provideQueryToFindIdsOfDocsSortedByRelevance(String searchTerms, Pageable pageable) {
        String actualQueryContent = FIND_MOST_RELEVANT_QUERY.replaceAll("\\?0", searchTerms);

        Pageable finalPageable = PagingUtils.addDescSort(pageable, "_score");

        return StringQuery.builder(actualQueryContent)
                .withPageable(finalPageable)
                .withFields(ArticleDocument.ElasticsearchFieldNames.ID)
                .withSourceFilter(excludeAllSourceFilter)
                .withTrackTotalHits(false)
                .build();
    }
}
