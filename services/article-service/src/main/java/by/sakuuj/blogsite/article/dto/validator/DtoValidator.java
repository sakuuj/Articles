package by.sakuuj.blogsite.article.dto.validator;

public interface DtoValidator {

    <T> void validate(T dto);
}
