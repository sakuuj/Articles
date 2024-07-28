package by.sakuuj.blogplatform.article.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class RestUtils {
    public static RestClient.RequestHeadersSpec<?> addBasicAuth(RestClient.RequestHeadersSpec<?> request,
                                                          String username,
                                                          String password) {
        byte[] credentials = (username + ":" + password)
                .getBytes(StandardCharsets.UTF_8);
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials);

        return request.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
    }

    public static boolean anyStatus(HttpStatusCode statusCode) {
        return true;
    }

    public static RestClient.ResponseSpec.ErrorHandler noOpHandler() {
        return (request, response) -> {};
    }
}
