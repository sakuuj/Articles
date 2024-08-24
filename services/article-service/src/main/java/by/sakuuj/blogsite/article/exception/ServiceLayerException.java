package by.sakuuj.blogsite.article.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ServiceLayerException extends RuntimeException {

    private final ExceptionMessage exceptionMessage;

    public ServiceLayerException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.name());

        this.exceptionMessage = exceptionMessage;
    }

    public ServiceLayerException(ExceptionMessage exceptionMessage, Throwable cause) {
        super(exceptionMessage.name(), cause);

        this.exceptionMessage = exceptionMessage;
    }
}
