package by.sakuuj.articles.article.controller;

import by.sakuuj.articles.article.error.ApiError;
import by.sakuuj.articles.article.exception.EntityNotFoundException;
import by.sakuuj.articles.article.exception.EntityVersionDoesNotMatch;
import by.sakuuj.articles.article.exception.IdempotencyTokenExistsException;
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

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

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

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            IdempotencyTokenExistsException.class,
            DataIntegrityViolationException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiError> handleInvalidRequest(Exception ex) {

        return ResponseEntity.badRequest()
                .body(ApiError.invalidRequest(ex.getMessage()));
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ApiError> handleHttpMethodNotSupported(Exception ex) {

        return ResponseEntity.badRequest()
                .body(ApiError.httpMethodNotSupported(ex.getMessage()));
    }

    @ExceptionHandler({
            OptimisticLockingFailureException.class,
            EntityVersionDoesNotMatch.class
    })
    public ResponseEntity<ApiError> handleOptimisticLockError(Exception ex) {

        return ResponseEntity.badRequest()
                .body(ApiError.optimisticLockingError(ex.getMessage()));
    }


    @ExceptionHandler({
            EntityNotFoundException.class,
    })
    public ResponseEntity<ApiError> handleEntityNotFoundError(Exception ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.notFoundError(ex.getMessage()));
    }
}
