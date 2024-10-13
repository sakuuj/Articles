package by.sakuuj.articles.article.configs;

import lombok.RequiredArgsConstructor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.articles.article.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private final ElasticsearchProperties elasticsearchProperties;

    private static final int REACTOR_IO_THREAD_COUNT = 1;

    @Override
    @SuppressWarnings("NullableProblems")
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperties.getUris().toArray(String[]::new))
                .withClientConfigurer(ElasticsearchClients.ElasticsearchHttpClientConfigurationCallback.from(
                        httpAsyncClientBuilder -> {
                            httpAsyncClientBuilder.setDefaultIOReactorConfig(
                                    IOReactorConfig
                                            .custom()
                                            .setIoThreadCount(REACTOR_IO_THREAD_COUNT)
                                            .build());
                            return httpAsyncClientBuilder;
                        }
                ))
                .withBasicAuth(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())
                .withSocketTimeout(elasticsearchProperties.getSocketTimeout())
                .withConnectTimeout(elasticsearchProperties.getConnectionTimeout())
                .build();
    }
}


