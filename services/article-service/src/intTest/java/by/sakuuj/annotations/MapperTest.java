package by.sakuuj.annotations;

import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@JpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackages = "by.sakuuj.blogsite.article.mapper")
public @interface MapperTest {
}
