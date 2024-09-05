package by.sakuuj.blogsite.exception;

public class ControllerLayerException extends RuntimeException {

    private final ControllerLayerExceptionMessage controllerLayerExceptionMessage;

    public ControllerLayerException(ControllerLayerExceptionMessage controllerLayerExceptionMessage) {
        super(controllerLayerExceptionMessage.name());

        this.controllerLayerExceptionMessage = controllerLayerExceptionMessage;
    }

    public ControllerLayerException(ControllerLayerExceptionMessage controllerLayerExceptionMessage, Throwable cause) {
        super(controllerLayerExceptionMessage.name(), cause);

        this.controllerLayerExceptionMessage = controllerLayerExceptionMessage;
    }
}
