package by.sakuuj.articles.person.server;

import by.sakuuj.articles.person.grpc.PersonServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PersonGrpcServer {

    @Value("${by.sakuuj.server.port:-1}")
    private int port;

    private final PersonServiceGrpc.PersonServiceImplBase personGrpcService;

    public void launchAndBlock() throws IOException, InterruptedException {

        Server server = ServerBuilder
                .forPort(port)
                .addService(personGrpcService)
                .build();

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }
}
