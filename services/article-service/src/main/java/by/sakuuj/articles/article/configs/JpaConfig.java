package by.sakuuj.articles.article.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EntityScan("by.sakuuj.articles.entity.jpa")
@EnableJpaRepositories(basePackages = {
        "by.sakuuj.articles.repository.jpa",
        "by.sakuuj.articles.article.repository.jpa",
})
public class JpaConfig {
}