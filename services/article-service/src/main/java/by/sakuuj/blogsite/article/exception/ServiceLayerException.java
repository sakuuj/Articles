package by.sakuuj.blogsite.article.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ServiceLayerException extends RuntimeException {

    private final ServiceLayerExceptionMessage serviceLayerExceptionMessage;

    public ServiceLayerException(ServiceLayerExceptionMessage serviceLayerExceptionMessage) {
        super(serviceLayerExceptionMessage.name());

        this.serviceLayerExceptionMessage = serviceLayerExceptionMessage;
    }

    public ServiceLayerException(ServiceLayerExceptionMessage serviceLayerExceptionMessage, Throwable cause) {
        super(serviceLayerExceptionMessage.name(), cause);

        this.serviceLayerExceptionMessage = serviceLayerExceptionMessage;
    }
}
