package by.sakuuj.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

public abstract class PostgresSingletonContainerLauncher {

    static final String USERNAME = "postgres";
    static final String PASSWORD = "postgres";
    static final String DATABASE = "postgres";

    static final GenericContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new GenericContainer<>("postgres:16.4")
                .withEnv("POSTGRES_PASSWORD", PASSWORD)
                .withEnv("POSTGRES_USER", USERNAME)
                .withEnv("POSTGRES_DB", DATABASE)
                .withExposedPorts(5432);

        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> List.of(getFullContainerUri()));
        registry.add("spring.datasource.username", () -> USERNAME);
        registry.add("spring.datasource.password", () -> PASSWORD);
    }

    static String getFullContainerUri() {
        return "jdbc:postgresql://" + POSTGRES_CONTAINER.getHost() + ":"
                + POSTGRES_CONTAINER.getFirstMappedPort()
                + "/" + DATABASE;
    }

}