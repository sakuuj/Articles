package by.sakuuj.blogsite.repostiory;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "by.sakuuj.blogsite.entity.jpa")
@EnableJpaRepositories(basePackages = "by.sakuuj.blogsite.repository.jpa")
public class TestConfig {
}
