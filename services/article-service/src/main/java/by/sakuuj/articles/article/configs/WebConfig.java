package by.sakuuj.articles.article.configs;

import by.sakuuj.articles.controller.resolvers.AuthenticatedUserArgumentResolver;
import by.sakuuj.articles.controller.resolvers.RequestedPageArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new RequestedPageArgumentResolver(false));
        resolvers.add(new AuthenticatedUserArgumentResolver());
    }
}
