package by.sakuuj.blogsite.article.configs;

import by.sakuuj.blogsite.controller.resolvers.AuthenticatedUserArgumentResolver;
import by.sakuuj.blogsite.controller.resolvers.RequestedPageArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new RequestedPageArgumentResolver());
        resolvers.add(new AuthenticatedUserArgumentResolver());
    }
}
