package by.sakuuj.blogsite.person.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = {
        "by.sakuuj.blogsite.person.repository",
        "by.sakuuj.blogsite.repository.jpa"
})
public class JpaConfig {
}
