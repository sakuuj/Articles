package by.sakuuj.articles.repostiory;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "by.sakuuj.articles.entity.jpa")
@EnableJpaRepositories(basePackages = "by.sakuuj.articles.repository.jpa")
public class TestConfig {
}
