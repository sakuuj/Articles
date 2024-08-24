package by.sakuuj.blogsite.article.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = "by.sakuuj.blogsite.article.repository.jpa")
public class JpaConfig {
}