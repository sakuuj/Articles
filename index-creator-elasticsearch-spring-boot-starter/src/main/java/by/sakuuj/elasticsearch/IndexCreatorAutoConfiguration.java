package by.sakuuj.elasticsearch;

import by.sakuuj.elasticsearch.file.ToStringFileReader;
import by.sakuuj.elasticsearch.file.ToStringFileReaderImpl;
import by.sakuuj.elasticsearch.http.client.BasicAuthenticationHandler;
import by.sakuuj.elasticsearch.http.client.HttpRequestAuthenticationHandler;
import by.sakuuj.elasticsearch.http.client.IndexCreatorElasticsearchClient;
import by.sakuuj.elasticsearch.http.client.IndexCreatorElasticsearchClientImpl;
import by.sakuuj.elasticsearch.json.JsonContentExtractor;
import by.sakuuj.elasticsearch.json.JsonContentExtractorImpl;
import by.sakuuj.elasticsearch.json.JsonValidator;
import by.sakuuj.elasticsearch.json.JsonValidatorImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = IndexCreatorProperties.class)
@ConditionalOnProperty(
        name = "by.sakuuj.elasticsearch.index-creator.enable",
        havingValue = "true",
        matchIfMissing = true
)
public class IndexCreatorAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = "by.sakuuj.elasticsearch.index-creator.run-on-startup",
            havingValue = "true",
            matchIfMissing = true
    )
    public CommandLineRunner createIndexesCommandLineRunner(IndexCreator indexCreator,
                                                            IndexCreatorProperties indexCreatorProperties) {

        List<Map.Entry<String, String>> indexToJsonFilePairs = indexCreatorProperties.getIndexToJsonFilePairs();
        return args -> indexCreator.createIndexes(indexToJsonFilePairs);
    }

    @Bean
    @ConditionalOnMissingBean(value = IndexCreator.class)
    public IndexCreator indexCreator(JsonContentExtractor jsonContentExtractor,
                                     IndexCreatorElasticsearchClient elasticsearchClient) {
        return new IndexCreatorImpl(jsonContentExtractor, elasticsearchClient);
    }

    @Bean
    @ConditionalOnMissingBean(value = IndexCreatorElasticsearchClient.class)
    public IndexCreatorElasticsearchClient indexCreatorElasticsearchClient(
            RestClient indexCreatorRestClient,
            HttpRequestAuthenticationHandler authenticationHandler,
            @Value("${by.sakuuj.elasticsearch.index-creator.uri}") String uri
    ) {

        return new IndexCreatorElasticsearchClientImpl(
                indexCreatorRestClient,
                authenticationHandler,
                uri);
    }

    @Bean(name = "index-creator-rest-client")
    @ConditionalOnMissingBean(value = RestClient.class)
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    @ConditionalOnProperty(
            name = "by.sakuuj.elasticsearch.index-creator.authentication",
            havingValue = "basic",
            matchIfMissing = true
    )
    @ConditionalOnMissingBean(value = HttpRequestAuthenticationHandler.class)
    public HttpRequestAuthenticationHandler basicAuthenticationHandler(
            @Value("${by.sakuuj.elasticsearch.index-creator.username}") String username,
            @Value("${by.sakuuj.elasticsearch.index-creator.password}") String password
    ) {
        return new BasicAuthenticationHandler(username, password);
    }

    @Bean
    @ConditionalOnMissingBean(value = JsonContentExtractor.class)
    public JsonContentExtractor jsonContentExtractor(JsonValidator jsonValidator, ToStringFileReader toStringFileReader) {
        return new JsonContentExtractorImpl(jsonValidator, toStringFileReader);
    }

    @Bean
    @ConditionalOnMissingBean(value = JsonValidator.class)
    public JsonValidator jsonValidator(ObjectMapper objectMapper) {
        return new JsonValidatorImpl(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(value = ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean(value = ToStringFileReader.class)
    public ToStringFileReader toStringFileReader() {
        return new ToStringFileReaderImpl();
    }
}
