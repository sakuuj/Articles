package by.sakuuj.blogsite.article.consumer;


import by.sakuuj.blogsite.article.ArticleDocumentTestBuilder;
import by.sakuuj.blogsite.article.ElasticsearchConsumerApplication;
import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.repository.elasticsearch.ArticleDocumentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@Import(TestConfig.class)
@SpringBootTest(classes = ElasticsearchConsumerApplication.class)
@Testcontainers
class ElasticsearchConsumerTests {

    private static final String KAFKA_KRAFT_IMAGE_NAME = "apache/kafka:latest";

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(KAFKA_KRAFT_IMAGE_NAME)
            .withEnv(Map.of(
                    "KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false"
            ));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    }

    @Autowired
    private KafkaTemplate<UUID, ArticleDocumentRequest> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateStringToString;

    @MockBean
    private ArticleDocumentRepository articleDocumentRepository;

    @SpyBean
    private DefaultErrorHandler defaultErrorHandler;

    @SpyBean
    private ElasticsearchConsumer elasticsearchConsumer;

    @Captor
    private ArgumentCaptor<ListenerExecutionFailedException> exceptionArgumentCaptor;

    @Captor
    private ArgumentCaptor<ConsumerRecord<UUID, ArticleDocumentRequest>> consumerRecordCaptor;

    @Test
    void shouldFetchFromCorrectTopicAndDeserializeValidData() throws ExecutionException, InterruptedException {

        ArticleDocument articleDocument = ArticleDocumentTestBuilder.anArticleDocument().buildDocument();

        ArticleDocumentRequest dataToSend = ArticleDocumentRequest.builder()
                .type(ArticleDocumentRequest.RequestType.UPSERT)
                .articleDocument(articleDocument)
                .build();

        kafkaTemplate.send(ElasticsearchConsumer.TOPIC_NAME, articleDocument.getId(), dataToSend).get();

        verify(elasticsearchConsumer, timeout(5000)).consumeArticle(consumerRecordCaptor.capture());

        ConsumerRecord<UUID, ArticleDocumentRequest> actual = consumerRecordCaptor.getValue();
        assertThat(actual.key()).isEqualTo(articleDocument.getId());
        assertThat(actual.value()).isEqualTo(dataToSend);
    }

    @Test
    void shouldRetryACoupleOfTimes() throws ExecutionException, InterruptedException {

        ArticleDocument articleDocument = ArticleDocumentTestBuilder.anArticleDocument().buildDocument();

        ArticleDocumentRequest dataToSend = ArticleDocumentRequest.builder()
                .type(ArticleDocumentRequest.RequestType.UPSERT)
                .articleDocument(articleDocument)
                .build();

        int exceptionCount = 2;

        doAnswer(new Answer<ArticleDocumentRequest>() {

            int count = 0;

            @Override
            public ArticleDocumentRequest answer(InvocationOnMock invocation) {

                if (count++ < exceptionCount) {
                    throw new RuntimeException();
                }
                return null;
            }
        }).when(articleDocumentRepository).save(articleDocument);

        kafkaTemplate.send(ElasticsearchConsumer.TOPIC_NAME, articleDocument.getId(), dataToSend).get();

        verify(elasticsearchConsumer, timeout(7000).times(exceptionCount + 1)).consumeArticle(consumerRecordCaptor.capture());

        ConsumerRecord<UUID, ArticleDocumentRequest> actual = consumerRecordCaptor.getValue();
        assertThat(actual.key()).isEqualTo(articleDocument.getId());
        assertThat(actual.value()).isEqualTo(dataToSend);
    }

    @Test
    void shouldFetchFromCorrectTopicAndNotDeserializeMalformedJson() throws ExecutionException, InterruptedException {

        kafkaTemplateStringToString.send(ElasticsearchConsumer.TOPIC_NAME, "hello").get();

        verify(defaultErrorHandler, timeout(5000)).handleRemaining(exceptionArgumentCaptor.capture(), any(), any(), any());

        ListenerExecutionFailedException caughtException = exceptionArgumentCaptor.getValue();
        assertThat(caughtException.getCause()).isInstanceOf(DeserializationException.class);
    }

    @Test
    void shouldFetchFromCorrectAndNotDeserializeDueToInvalidDto() throws ExecutionException, InterruptedException {

        ArticleDocumentRequest invalidDto = ArticleDocumentRequest.builder()
                .type(null)
                .articleDocument(null)
                .build();

        kafkaTemplate.send(ElasticsearchConsumer.TOPIC_NAME, invalidDto).get();

        verify(defaultErrorHandler, timeout(5000)).handleRemaining(exceptionArgumentCaptor.capture(), any(), any(), any());

        ListenerExecutionFailedException caughtException = exceptionArgumentCaptor.getValue();
        assertThat(caughtException.getCause()).isInstanceOf(DeserializationException.class);
    }
}
