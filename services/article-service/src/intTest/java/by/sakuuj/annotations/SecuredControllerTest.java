package by.sakuuj.annotations;

import by.sakuuj.blogsite.article.configs.SecurityConfig;
import by.sakuuj.blogsite.configs.GrpcConfig;
import by.sakuuj.blogsite.service.PersonServiceImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SecurityConfig.class, GrpcConfig.class, PersonServiceImpl.class})
@ComponentScan(basePackages = {
        "by.sakuuj.blogsite.security",
        "by.sakuuj.blogsite.controller.resolvers"
})
public @interface SecuredControllerTest {
}
