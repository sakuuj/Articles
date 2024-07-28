package by.sakuuj.blogplatform.article.configs.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used to prevent deadlock when using virtual threads
 */
@Aspect
@Component
@ConditionalOnProperty(name = "spring.threads.virtual.enabled")
public class ElasticsearchFixDeadlockAspect {

    private final Lock lock = new ReentrantLock();

    @Around("within(org.elasticsearch.client.RestClient)")
    public Object fixDeadlock(ProceedingJoinPoint pjp) throws Throwable {
        try {
            lock.lock();
            return pjp.proceed(pjp.getArgs());
        } finally {
            lock.unlock();
        }
    }
}
