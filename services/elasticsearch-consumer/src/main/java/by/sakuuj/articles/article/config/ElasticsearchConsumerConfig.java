package by.sakuuj.articles.article.config;

import by.sakuuj.articles.article.consumer.ElasticsearchConsumer;
import by.sakuuj.articles.article.dto.ArticleDocumentRequest;
import by.sakuuj.articles.article.exceptions.RecordKeyIsAbsentException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class ElasticsearchConsumerConfig {

    private static final int MAX_RETRIES = 10;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<UUID, ArticleDocumentRequest>> kafkaListenerContainerFactory(
            ConsumerFactory<UUID, ArticleDocumentRequest> consumerFactory,
            DefaultErrorHandler defaultErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<UUID, ArticleDocumentRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(6);
        factory.setCommonErrorHandler(defaultErrorHandler);

        return factory;
    }

    @Bean
    public ConsumerFactory<UUID, ArticleDocumentRequest> consumerFactory(KafkaProperties kafkaProperties, Validator validator) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);

        props.put(JsonDeserializer.TYPE_MAPPINGS,
                ElasticsearchConsumer.CONSUMED_TYPE_NAME + ":" + ArticleDocumentRequest.class.getName()
        );

        props.put(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                "org.apache.kafka.clients.consumer.CooperativeStickyAssignor"
        );

        return new DefaultKafkaConsumerFactory<>(props,
                () -> {
                    var keyDeserializer = new ErrorHandlingDeserializer<>(new UUIDDeserializer());
                    keyDeserializer.setValidator(validator);
                    return keyDeserializer;
                },
                () -> {
                    var delegateValueDeserializer = new JsonDeserializer<ArticleDocumentRequest>();

                    var valueDeserializer = new ErrorHandlingDeserializer<>(delegateValueDeserializer);
                    valueDeserializer.setValidator(validator);

                    return valueDeserializer;
                }
        );

    }

    @Bean
    public BackOff exponentialBackOff() {

        return new ExponentialBackOffWithMaxRetries(MAX_RETRIES);
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(BackOff backOff) {

        var errorHandler = new DefaultErrorHandler(backOff);
        errorHandler.addNotRetryableExceptions(RecordKeyIsAbsentException.class);

        return errorHandler;
    }

}
