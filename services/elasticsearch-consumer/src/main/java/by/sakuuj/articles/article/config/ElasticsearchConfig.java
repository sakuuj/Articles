package by.sakuuj.articles.article.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration(proxyBeanMethods = false)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.articles.article.repository.elasticsearch")
public class ElasticsearchConfig {
}
