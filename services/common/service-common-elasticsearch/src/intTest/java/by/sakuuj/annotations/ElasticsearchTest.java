package by.sakuuj.annotations;

import by.sakuuj.elasticsearch.IndexCreatorAutoConfiguration;
import by.sakuuj.testconfigs.ElasticsearchTestConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@DataElasticsearchTest
@ContextConfiguration(classes = ElasticsearchTestConfig.class)
@EnableAutoConfiguration
@Import(IndexCreatorAutoConfiguration.class)
@EnableElasticsearchRepositories(basePackages = "by.sakuuj.blogsite.article.repository.elasticsearch")
@ComponentScan(basePackages = {
        "by.sakuuj.blogsite.article.entity.elasticsearch",
        "by.sakuuj.blogsite.article.repository.elasticsearch"
})
public @interface ElasticsearchTest {

}
