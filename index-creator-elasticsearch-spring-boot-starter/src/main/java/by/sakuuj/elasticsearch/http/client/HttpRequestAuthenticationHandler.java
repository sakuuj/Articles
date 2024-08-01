package by.sakuuj.elasticsearch.http.client;

import org.springframework.web.client.RestClient;

public interface HttpRequestAuthenticationHandler {

    void authenticate(RestClient.RequestHeadersSpec<?> request);
}
