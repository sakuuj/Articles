package by.sakuuj.annotations;

import by.sakuuj.blogsite.article.ArticleServiceApplication;
import by.sakuuj.blogsite.article.configs.ElasticsearchConfig;
import by.sakuuj.blogsite.article.configs.aop.ElasticsearchFixDeadlockAspect;
import by.sakuuj.elasticsearch.IndexCreatorAutoConfiguration;
import by.sakuuj.testconfigs.ElasticsearchTestConfig;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@DataElasticsearchTest
@Import({
        IndexCreatorAutoConfiguration.class,

        ElasticsearchConfig.class,
        ElasticsearchTestConfig.class,

        AopAutoConfiguration.class,
        ElasticsearchFixDeadlockAspect.class,
})
@ContextConfiguration(classes = ArticleServiceApplication.class)
public @interface ElasticsearchTest {

}
