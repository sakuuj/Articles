package by.sakuuj.annotations;

import by.sakuuj.elasticsearch.IndexCreatorAutoConfiguration;
import by.sakuuj.testconfigs.EmptyConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@DataElasticsearchTest
@ContextConfiguration(classes = EmptyConfig.class)
@ImportAutoConfiguration(IndexCreatorAutoConfiguration.class)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.articles.article.repository.elasticsearch")
@ComponentScan(basePackages = {
        "by.sakuuj.articles.article.entity.elasticsearch",
        "by.sakuuj.articles.article.repository.elasticsearch"
})
public @interface ElasticsearchTest {

}
