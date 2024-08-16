package by.sakuuj.blogsite.article.repository.elasticsearch;

import by.sakuuj.blogsite.article.entities.elasticsearch.ArticleDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryProviderImplTests {

    private final QueryProvider queryProvider = new QueryProviderImpl();

    private final ObjectMapper objectMapperForTests = new ObjectMapper();

    @MethodSource
    @ParameterizedTest
    void shouldHaveCorrectQueryPropertiesAfterCallingProvideQueryToFindIdsOfDocsSortedByRelevance(Sort providedSort) {
        // given
        int pageNumber = 4;
        int pageSize = 44;

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, providedSort
        );

        Sort expectedSort = providedSort.and(Sort.by("_score").descending());
        int expectedPageNumber = pageNumber;
        int expectedPageSize = pageSize;
        boolean expectedTrackTotalHits = false;
        String[] expectedExcludesOnSourceFilter = new String[]{"*"};
        String[] expectedIncludesOnSourceFilter = new String[]{};

        String searchTerms = "first second";

        // when
        StringQuery actual = queryProvider.provideQueryToFindIdsOfDocsSortedByRelevance(searchTerms, pageable);

        // then
        assertThat(actual.getTrackTotalHits()).isEqualTo(expectedTrackTotalHits);
        assertThat(actual.getSort()).isEqualTo(expectedSort);
        assertThat(actual.getPageable().getPageNumber()).isEqualTo(expectedPageNumber);
        assertThat(actual.getPageable().getPageSize()).isEqualTo(expectedPageSize);

        SourceFilter sourceFilter = actual.getSourceFilter();
        assertThat(sourceFilter).isNotNull();
        assertThat(sourceFilter.getExcludes()).isEqualTo(expectedExcludesOnSourceFilter);
        assertThat(sourceFilter.getIncludes()).isEqualTo(expectedIncludesOnSourceFilter);
    }

    static List<Sort> shouldHaveCorrectQueryPropertiesAfterCallingProvideQueryToFindIdsOfDocsSortedByRelevance() {
        return List.of(
                Sort.unsorted(),
                Sort.by(ArticleDocument.ElasticsearchFieldNames.DATE_PUBLISHED).descending()
        );
    }

    @Test
    void shouldHaveCorrectQueryContentAfterCallingProvideQueryToFindIdsOfDocsSortedByRelevance() throws Exception {
        // given
        int pageNumber = 4;
        int pageSize = 44;
        Pageable pageable = PageRequest.of(
                pageNumber, pageSize
        );

        String searchTerms = "first second";

        // when
        StringQuery actualQuery = queryProvider.provideQueryToFindIdsOfDocsSortedByRelevance(searchTerms, pageable);
        String actualQueryContent = actualQuery.getSource();

        // then
        JsonNode actualQueryContentJackson = objectMapperForTests.readTree(actualQueryContent);

        String expectedQueryContent = String.format("""
                {
                    "bool": {
                      "should": [
                        {
                          "match": {
                            "title": {
                              "boost": 1.5,
                              "query": "%s",
                              "fuzziness": "AUTO"
                            }
                          }
                        },
                        {
                          "match": {
                            "content": {
                              "query": "%s",
                              "fuzziness": "AUTO"
                            }
                          }
                        }
                      ],
                      "minimum_should_match": 1
                    }
                }
                """, searchTerms, searchTerms);

        JsonNode expectedQueryContentJackson = objectMapperForTests.readTree(expectedQueryContent);

        assertThat(actualQueryContentJackson).isEqualTo(expectedQueryContentJackson);
    }
}

