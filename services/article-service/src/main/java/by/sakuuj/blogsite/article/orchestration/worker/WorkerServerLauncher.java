package by.sakuuj.blogsite.article.orchestration.worker;

import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerServerLauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final WorkerFactory workerFactory;
    private final AsyncTaskExecutor asyncExecutor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent ignored) {

        asyncExecutor.execute(workerFactory::start);
        Runtime.getRuntime().addShutdownHook(new Thread(workerFactory::shutdown));
    }
}
