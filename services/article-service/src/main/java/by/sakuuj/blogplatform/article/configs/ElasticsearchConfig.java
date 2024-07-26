package by.sakuuj.blogplatform.article.configs;

import by.sakuuj.blogplatform.article.converters.CharArrayToStringConverter;
import by.sakuuj.blogplatform.article.converters.StringToCharArrayConverter;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;
import java.util.Arrays;

@Configuration(proxyBeanMethods = false)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.blogplatform.article.repositories.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private static final int REACTOR_IO_THREAD_COUNT = 1;
    private static final Duration SOCKET_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(Arrays.asList(
                new StringToCharArrayConverter(),
                new CharArrayToStringConverter()
        ));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public ClientConfiguration clientConfiguration() {

        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
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
                .withBasicAuth(username, password)
                .withSocketTimeout(SOCKET_TIMEOUT)
                .withConnectTimeout(CONNECT_TIMEOUT)
                .build();
    }
}


