package by.sakuuj.articles.article.exception;

public class EntityVersionDoesNotMatch extends RuntimeException {

    public EntityVersionDoesNotMatch() {
        super("Entity version does not match");
    }

    public EntityVersionDoesNotMatch(String message) {
        super(message);
    }
}
