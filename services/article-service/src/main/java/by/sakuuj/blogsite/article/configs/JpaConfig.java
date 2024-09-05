package by.sakuuj.blogsite.article.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EntityScan("by.sakuuj.blogsite.entity.jpa")
@EnableJpaRepositories(basePackages = {
        "by.sakuuj.blogsite.repository.jpa",
        "by.sakuuj.blogsite.article.repository.jpa",
})
public class JpaConfig {
}