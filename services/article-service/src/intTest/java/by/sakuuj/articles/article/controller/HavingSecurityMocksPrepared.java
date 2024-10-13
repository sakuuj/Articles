package by.sakuuj.articles.article.controller;

import by.sakuuj.articles.security.AuthenticatedUserAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public abstract class HavingSecurityMocksPrepared {

    @SpyBean
    private AuthenticatedUserAuthenticationFilter authenticatedUserAuthenticationFilter;

    @BeforeEach
    void configureAuthenticatedUserAuthenticationFilter() throws ServletException, IOException {

        doAnswer(invocation -> {

            ServletRequest servletRequest = invocation.getArgument(0, ServletRequest.class);
            ServletResponse servletResponse = invocation.getArgument(1, ServletResponse.class);
            FilterChain filterChain = invocation.getArgument(2, FilterChain.class);
            filterChain.doFilter(servletRequest, servletResponse);

            return null;

        }).when(authenticatedUserAuthenticationFilter).doFilter(any(), any(), any());
    }
}
