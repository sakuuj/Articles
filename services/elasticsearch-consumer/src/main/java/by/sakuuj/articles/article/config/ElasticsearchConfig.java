package by.sakuuj.articles.article.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.articles.article.repository.elasticsearch")
public class ElasticsearchConfig  {



}

