package by.sakuuj.annotations;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JpaTest
@ComponentScan(basePackages = "by.sakuuj.articles.article.mapper.jpa")
public @interface JpaMapperTest {
}
