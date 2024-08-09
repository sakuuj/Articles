package by.sakuuj.blogplatform.article.repository.elasticsearch;

import by.sakuuj.blogplatform.article.ArticleServiceApplication;
import by.sakuuj.blogplatform.article.entities.ArticleDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration(exclude = {
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
@SpringBootTest(classes = ArticleServiceApplication.class)
public class QueryBuilderTests {

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @MethodSource
    @ParameterizedTest
    void shouldHaveCorrectQueryPropertiesAfterCallingBuildQueryToFindIdsOfDocsSortedByRelevance(Sort providedSort) {
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
        StringQuery actual = queryBuilder.buildQueryToFindIdsOfDocsSortedByRelevance(searchTerms, pageable);

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

    static List<Sort> shouldHaveCorrectQueryPropertiesAfterCallingBuildQueryToFindIdsOfDocsSortedByRelevance() {
        return List.of(
                Sort.unsorted(),
                Sort.by(ArticleDocument.ElasticsearchFieldNames.DATE_PUBLISHED).descending()
        );
    }

    @Test
    void shouldHaveCorrectQueryContentAfterCallingBuildQueryToFindIdsOfDocsSortedByRelevance() throws Exception {
        // given
        int pageNumber = 4;
        int pageSize = 44;
        Pageable pageable = PageRequest.of(
                pageNumber, pageSize
        );

        String searchTerms = "first second";

        // when
        StringQuery actualQuery = queryBuilder.buildQueryToFindIdsOfDocsSortedByRelevance(searchTerms, pageable);
        String actualQueryContent = actualQuery.getSource();

        // then
        JsonNode actualQueryContentJackson = objectMapper.readTree(actualQueryContent);

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

        JsonNode expectedQueryContentJackson = objectMapper.readTree(expectedQueryContent);

        assertThat(actualQueryContentJackson).isEqualTo(expectedQueryContentJackson);
    }
}

