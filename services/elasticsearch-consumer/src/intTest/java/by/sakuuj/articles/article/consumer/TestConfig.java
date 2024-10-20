package by.sakuuj.articles.article.consumer;

import by.sakuuj.articles.article.dto.ArticleDocumentRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.UUID;

@TestConfiguration
class TestConfig {

    @Bean
    public NewTopic articlesTopic() {

        return TopicBuilder.name(ElasticsearchConsumer.TOPIC_NAME)
                .replicas(1)
                .partitions(3)
                .build();
    }

    @Bean
    public ProducerFactory<UUID, ArticleDocumentRequest> producerFactory(KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS,
                ElasticsearchConsumer.CONSUMED_TYPE_NAME + ":" + ArticleDocumentRequest.class.getName()
        );


        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<UUID, ArticleDocumentRequest> kafkaTemplate(
            ProducerFactory<UUID, ArticleDocumentRequest> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ProducerFactory<String, String> producerFactoryStringToString(KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS,
                ElasticsearchConsumer.CONSUMED_TYPE_NAME + ":" + String.class.getName()
        );

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateStringToString(ProducerFactory<String, String> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

}
