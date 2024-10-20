package by.sakuuj.articles.article.controller;

import by.sakuuj.articles.article.error.ApiError;
import by.sakuuj.articles.article.exception.EntityNotFoundException;
import by.sakuuj.articles.article.exception.EntityVersionDoesNotMatch;
import by.sakuuj.articles.article.exception.IdempotencyTokenExistsException;
import io.temporal.failure.ApplicationFailure;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    private static final Map<Class<?>, Function<String, ResponseEntity<ApiError>>> exceptionsToGetterFunctions = Map.of(

            ConstraintViolationException.class, RestControllerExceptionHandler::getInvalidRequestError,
            MethodArgumentTypeMismatchException.class, RestControllerExceptionHandler::getInvalidRequestError,
            MethodArgumentNotValidException.class, RestControllerExceptionHandler::getInvalidRequestError,
            IdempotencyTokenExistsException.class, RestControllerExceptionHandler::getInvalidRequestError,
            DataIntegrityViolationException.class, RestControllerExceptionHandler::getInvalidRequestError,
            IllegalStateException.class, RestControllerExceptionHandler::getInvalidRequestError,

            HttpRequestMethodNotSupportedException.class, RestControllerExceptionHandler::getHttpMethodNotSupportedError,

            EntityNotFoundException.class, RestControllerExceptionHandler::getEntityNotFoundError,

            OptimisticLockingFailureException.class, RestControllerExceptionHandler::getOptimisticLockError,
            EntityVersionDoesNotMatch.class, RestControllerExceptionHandler::getOptimisticLockError
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleNotHandled(Exception ex) {

        Throwable t = ex;
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(t.getMessage());
            sb.append("\n");
        } while ((t = t.getCause()) != null);

        return ResponseEntity.internalServerError()
                .body(ApiError.internalError(sb.toString()));
    }

    @ExceptionHandler(ApplicationFailure.class)
    public ResponseEntity<ApiError> handleTemporalApplicationFailure(ApplicationFailure applicationFailure) {

        String underlyingExceptionTypeName = applicationFailure.getType();
        try {
            Class<?> underlyingExceptionClass = Class.forName(underlyingExceptionTypeName);
            Function<String, ResponseEntity<ApiError>> handler = exceptionsToGetterFunctions.get(underlyingExceptionClass);

            if (handler != null) {
                return handler.apply(applicationFailure.getOriginalMessage());
            }
            return ResponseEntity.internalServerError()
                    .body(ApiError.internalError(applicationFailure.getOriginalMessage()));

        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ApiError.internalError(ex.getMessage()));
        }
    }

    private static ResponseEntity<ApiError> getInvalidRequestError(String errorMsg) {

        return ResponseEntity.badRequest()
                .body(ApiError.invalidRequest(errorMsg));
    }

    private static ResponseEntity<ApiError> getHttpMethodNotSupportedError(String errorMsg) {

        return ResponseEntity.badRequest()
                .body(ApiError.httpMethodNotSupported(errorMsg));
    }

    private static ResponseEntity<ApiError> getOptimisticLockError(String errorMsg) {

        return ResponseEntity.badRequest()
                .body(ApiError.optimisticLockingError(errorMsg));
    }


    private static ResponseEntity<ApiError> getEntityNotFoundError(String errorMsg) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.notFoundError(errorMsg));
    }
}
