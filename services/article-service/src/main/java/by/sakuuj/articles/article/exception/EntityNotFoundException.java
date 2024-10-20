package by.sakuuj.articles.article.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super("Entity was not found");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
