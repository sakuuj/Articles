package by.sakuuj.blogplatform.concurrency.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.Future;

@UtilityClass
public class FutureUtils {

    public static <T> T getResult(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
