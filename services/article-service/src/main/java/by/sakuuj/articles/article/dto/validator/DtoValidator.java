package by.sakuuj.articles.article.dto.validator;

public interface DtoValidator {

    <T> void validate(T dto);
}
