package by.sakuuj.articles.configs;

import by.sakuuj.articles.person.grpc.PersonServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GrpcConfig {

    @Bean
    public PersonServiceGrpc.PersonServiceBlockingStub personServiceBlockingStub(
            @Value("${by.sakuuj.person-grpc-server.target}")
            String personGrpcServerTarget
    ) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(personGrpcServerTarget)
                .usePlaintext()
                .build();

        return PersonServiceGrpc.newBlockingStub(channel);
    }
}
