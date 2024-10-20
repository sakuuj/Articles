package by.sakuuj.articles.person.server;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonGrpcServerLauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final PersonGrpcServer personGrpcServer;
    private final AsyncTaskExecutor asyncExecutor;

    /**
     * Launches the gRPC server and awaits its termination in a separate
     * thread using {@link AsyncTaskExecutor} in order to let Spring
     * finish the initialization of {@link org.springframework.context.ApplicationContext}.
     *
     * @param event not used
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        asyncExecutor.execute(() -> {
            try {
                personGrpcServer.launchAndBlock();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
