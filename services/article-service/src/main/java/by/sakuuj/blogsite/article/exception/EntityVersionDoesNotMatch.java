package by.sakuuj.blogsite.article.exception;

public class EntityVersionDoesNotMatch extends RuntimeException {

    public EntityVersionDoesNotMatch() {

    }

    public EntityVersionDoesNotMatch(String message) {
        super(message);
    }
}
