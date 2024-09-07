package by.sakuuj.blogsite.article.configs;

import by.sakuuj.blogsite.security.AuthenticatedUserAuthenticationFilter;
import by.sakuuj.blogsite.service.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    AuthenticatedUserAuthenticationFilter authenticatedUserAuthenticationFilter(PersonService personService) {
        return new AuthenticatedUserAuthenticationFilter(personService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            AuthenticatedUserAuthenticationFilter authenticatedUserAuthenticationFilter
    ) throws Exception {

        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                        )
                )
                .addFilterAfter(
                        authenticatedUserAuthenticationFilter,
                        BearerTokenAuthenticationFilter.class
                );

        return httpSecurity.build();
    }
}
