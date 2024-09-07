package by.sakuuj.blogsite.article.exception;

public enum ServiceLayerExceptionMessage {

    UPDATE_FAILED__ENTITY_NOT_FOUND,
    
    OPERATION_FAILED__ENTITY_VERSION_DOES_NOT_MATCH,

    OPERATION_FAILED__INVALID_DTO,

    CREATE_FAILED__IDEMPOTENCY_TOKEN_ALREADY_EXISTS
}