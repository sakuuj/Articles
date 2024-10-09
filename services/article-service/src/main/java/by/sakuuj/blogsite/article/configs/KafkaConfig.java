package by.sakuuj.blogsite.article.configs;

import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class KafkaConfig {

    @Bean
    public NewTopic articlesTopics(
            @Value("${by.sakuuj.blogsite.article.kafka.topic-name}") String topicName,
            @Value("${by.sakuuj.blogsite.article.kafka.replicas}") int replicas,
            @Value("${by.sakuuj.blogsite.article.kafka.partitions}") int partitions
            ) {

        return TopicBuilder.name(topicName)
                .replicas(replicas)
                .partitions(partitions)
                .build();
    }

    @Bean
    public ProducerFactory<UUID, ArticleDocumentRequest> producerFactory(KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS, "articleDocReq:" + ArticleDocumentRequest.class.getName());


        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<UUID, ArticleDocumentRequest> kafkaTemplate(
            ProducerFactory<UUID, ArticleDocumentRequest> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}
