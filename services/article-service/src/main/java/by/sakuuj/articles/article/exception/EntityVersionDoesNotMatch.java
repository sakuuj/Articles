package by.sakuuj.articles.article.exception;

public class EntityVersionDoesNotMatch extends RuntimeException {

    public EntityVersionDoesNotMatch() {

    }

    public EntityVersionDoesNotMatch(String message) {
        super(message);
    }
}
