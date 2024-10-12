package by.sakuuj.blogsite.article.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiError {

    private final int errorCode;
    private final String message;
    private final String detailedMessage;

    public static ApiError internalError(String detailedMessage) {

        return  ApiError.builder()
                .errorCode(-1)
                .message("Internal error")
                .detailedMessage(detailedMessage)
                .build();
    }

    public static ApiError invalidRequest(String detailedMessage) {

        return  ApiError.builder()
                .errorCode(1)
                .message("Invalid request")
                .detailedMessage(detailedMessage)
                .build();
    }

    public static ApiError httpMethodNotSupported(String detailedMessage) {

        return  ApiError.builder()
                .errorCode(2)
                .message("Http method not supported")
                .detailedMessage(detailedMessage)
                .build();
    }

    public static ApiError optimisticLockingError(String detailedMessage) {

        return  ApiError.builder()
                .errorCode(3)
                .message("Optimistic locking error")
                .detailedMessage(detailedMessage)
                .build();
    }

    public static ApiError notFoundError(String detailedMessage) {

        return  ApiError.builder()
                .errorCode(4)
                .message("Not found error")
                .detailedMessage(detailedMessage)
                .build();
    }
}
