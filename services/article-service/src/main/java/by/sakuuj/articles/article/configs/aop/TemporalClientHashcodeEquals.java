package by.sakuuj.articles.article.configs.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used to prevent deadlock when using virtual threads
 */
//@Aspect
//@Component
//@ConditionalOnProperty(name = "spring.threads.virtual.enabled")
public class TemporalClientHashcodeEquals {

    private final Lock lock = new ReentrantLock();

    @Around("execution(by.sakuuj.articles.article.orchestration.workflows.CreateArticleWorkflow)")
    public Object fixDeadlock(ProceedingJoinPoint pjp) throws Throwable {
        try {
            lock.lock();
            return pjp.proceed(pjp.getArgs());
        } finally {
            lock.unlock();
        }
    }
}
