package by.sakuuj.blogsite.concurrency.utils;


import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@UtilityClass
public class ExecutorUtils {

    public static <T> Future<T> submit(Executor executor, Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        executor.execute(future);
        return future;
    }
}
