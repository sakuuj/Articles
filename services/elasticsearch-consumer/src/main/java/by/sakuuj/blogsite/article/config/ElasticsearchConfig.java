package by.sakuuj.blogsite.article.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration(proxyBeanMethods = false)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.blogsite.article.repository.elasticsearch")
public class ElasticsearchConfig {
}
