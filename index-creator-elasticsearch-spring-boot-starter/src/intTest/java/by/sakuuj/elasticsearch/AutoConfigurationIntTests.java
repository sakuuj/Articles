package by.sakuuj.elasticsearch;

import by.sakuuj.elasticsearch.file.ToStringFileReader;
import by.sakuuj.elasticsearch.http.client.HttpRequestAuthenticationHandler;
import by.sakuuj.elasticsearch.http.client.IndexCreatorElasticsearchClient;
import by.sakuuj.elasticsearch.json.JsonContentExtractor;
import by.sakuuj.elasticsearch.json.JsonValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

public class AutoConfigurationIntTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(IndexCreatorAutoConfiguration.class)
            );

    @Test
    void shouldAutoConfigureIndexCreatorWithoutAnyConfigProvided_If_IndexesArePresentAndCreatorIsEnabled() {

        this.contextRunner
                .withSystemProperties()
                .withPropertyValues(
                        "by.sakuuj.elasticsearch.index-creator.uri=http://127.0.0.1:9200",
                        "by.sakuuj.elasticsearch.index-creator.username=elastic",
                        "by.sakuuj.elasticsearch.index-creator.password=elastic",
                        String.format("%s=%s,%s", "by.sakuuj.elasticsearch.index-creator.index-to-json-file-pairs",
                                "index name 1<->query path 1",
                                "index name 2<->query path 2")
                )
                .withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
                .run((context) -> {
                    assertThat(context).hasSingleBean(IndexCreator.class);
                    assertThat(context).hasSingleBean(IndexCreatorProperties.class);
                    assertThat(context).hasSingleBean(IndexCreatorElasticsearchClient.class);
                    assertThat(context).hasSingleBean(HttpRequestAuthenticationHandler.class);
                    assertThat(context).hasSingleBean(JsonValidator.class);
                    assertThat(context).hasSingleBean(JsonContentExtractor.class);
                    assertThat(context).hasSingleBean(ToStringFileReader.class);

                    var indexCreatorProperties = context.getBean(IndexCreatorProperties.class);
                    assertThat(indexCreatorProperties.getIndexToJsonFilePairs())
                            .containsExactlyElementsOf(List.of(
                                    entry("index name 1", "query path 1"),
                                    entry("index name 2", "query path 2")
                            ));
                });
    }

    @Test
    void shouldNotAutoConfigureIndexCreatorWithoutAnyConfigProvided_If_CreatorIsDisabled() {
        this.contextRunner
                .withPropertyValues(
                        "by.sakuuj.elasticsearch.index-creator.enable", "false",
                        "by.sakuuj.elasticsearch.index-creator.uri", "http://127.0.0.1:9200",
                        "by.sakuuj.elasticsearch.index-creator.username", "elastic",
                        "by.sakuuj.elasticsearch.index-creator.password", "elastic",
                        String.format("%s=%s,%s", "by.sakuuj.elasticsearch.index-creator.index-to-json-file-pairs",
                                "index name 1<->query path 1",
                                "index name 2<->query path 2")

                )
                .withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(IndexCreator.class);
                    assertThat(context).doesNotHaveBean(IndexCreatorProperties.class);
                    assertThat(context).doesNotHaveBean(IndexCreatorElasticsearchClient.class);
                    assertThat(context).doesNotHaveBean(HttpRequestAuthenticationHandler.class);
                    assertThat(context).doesNotHaveBean(JsonValidator.class);
                    assertThat(context).doesNotHaveBean(JsonContentExtractor.class);
                    assertThat(context).doesNotHaveBean(ToStringFileReader.class);
                });
    }


}
