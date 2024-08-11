package by.sakuuj.elasticsearch.http.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicAuthenticationHandlerTests {

    // given
    private final String username = "user1";
    private final String password = "123456";
    private final String expectedAuthorizationHeaderValue = "Basic dXNlcjE6MTIzNDU2";

    @Spy
    private BasicAuthenticationHandler basicAuthenticationHandler = new BasicAuthenticationHandler(username, password);
    @Captor
    private ArgumentCaptor<String> authorizationHeaderCaptor;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpecArgument;

    @Test
    void should() {

        // when
        basicAuthenticationHandler.authenticate(requestHeadersSpecArgument);

        // then
        verify(basicAuthenticationHandler).authenticate(requestHeadersSpecArgument);
        verify(requestHeadersSpecArgument).header(eq(HttpHeaders.AUTHORIZATION), authorizationHeaderCaptor.capture());

        String actualAuthorizationHeaderValue = authorizationHeaderCaptor.getValue();
        assertThat(actualAuthorizationHeaderValue).isEqualTo(expectedAuthorizationHeaderValue);
    }
}
