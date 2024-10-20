package by.sakuuj.articles.person.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = {
        "by.sakuuj.articles.person.repository",
        "by.sakuuj.articles.repository.jpa"
})
public class JpaConfig {
}
