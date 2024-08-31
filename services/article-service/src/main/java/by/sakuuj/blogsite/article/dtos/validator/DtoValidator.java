package by.sakuuj.blogsite.article.dtos.validator;

public interface DtoValidator {

    <T> void validate(T dto);
}
