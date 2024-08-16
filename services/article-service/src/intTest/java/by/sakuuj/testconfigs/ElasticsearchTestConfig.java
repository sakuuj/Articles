package by.sakuuj.testconfigs;


import by.sakuuj.blogplatform.article.repository.elasticsearch.QueryProvider;
import by.sakuuj.blogplatform.article.repository.elasticsearch.QueryProviderImpl;
import by.sakuuj.blogplatform.article.repository.elasticsearch.SearchHitsToPageViewMapper;
import by.sakuuj.blogplatform.article.repository.elasticsearch.SearchHitsToPageViewMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ElasticsearchTestConfig {

    @Bean
    public QueryProvider queryProvider() {
        return new QueryProviderImpl();
    }

    @Bean
    public SearchHitsToPageViewMapper searchHitsToPageViewMapper() {
        return new SearchHitsToPageViewMapperImpl();
    }
}
