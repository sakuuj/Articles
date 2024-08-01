package by.sakuuj.elasticsearch.http.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
public class BasicAuthenticationHandler implements HttpRequestAuthenticationHandler {

    private final String username;
    private final String password;

    @Override
    public void authenticate(RestClient.RequestHeadersSpec<?> request) {
        byte[] credentials = (username + ":" + password)
                .getBytes(StandardCharsets.UTF_8);
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials);

        request.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
    }
}
