package by.sakuuj.annotations;

import by.sakuuj.articles.article.configs.FormatConfig;
import by.sakuuj.articles.article.mapper.LocalDateTimeMapperImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JpaTest
@Import({LocalDateTimeMapperImpl.class, FormatConfig.class})
@ComponentScan(basePackages = "by.sakuuj.articles.article.mapper.jpa")
public @interface JpaMapperTest {
}
