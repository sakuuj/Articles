package by.sakuuj.blogsite.article.configs;

import by.sakuuj.blogsite.article.utils.CompileTimeConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

    @Bean
    public RestClient restClient(ClientHttpRequestFactory requestFactory) {
        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultStatusHandler(s -> true, (request, response) -> {})
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        return new JdkClientHttpRequestFactory(httpClient);
    }

    @Bean
    public HttpClient httpClient(@Qualifier(CompileTimeConstants.EXECUTOR_BEAN_NAME)
                                 Executor executor) {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .executor(executor)
                .build();
    }


}
