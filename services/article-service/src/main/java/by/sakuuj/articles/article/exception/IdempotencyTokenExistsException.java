package by.sakuuj.articles.article.exception;

public class IdempotencyTokenExistsException extends RuntimeException {

    public IdempotencyTokenExistsException() {
        super("Idempotency token already exists");
    }

    public IdempotencyTokenExistsException(String message) {
        super(message);
    }
}
