package by.sakuuj.blogsite.article.dtos.validator;

public interface DtoValidator {

    <T> void validateAndThrowIfInvalid(T dto);
}
