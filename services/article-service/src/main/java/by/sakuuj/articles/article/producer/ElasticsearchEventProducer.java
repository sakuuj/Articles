package by.sakuuj.articles.article.producer;


import by.sakuuj.articles.article.dto.ArticleDocumentRequest;
import by.sakuuj.articles.article.entity.elasticsearch.ArticleDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ElasticsearchEventProducer {

    @Value("${by.sakuuj.articles.article.kafka.topic-name}")
    private String elasticsearchEventTopic;

    private final KafkaTemplate<UUID, ArticleDocumentRequest> kafkaTemplate;

    public void produce(ArticleDocumentRequest.RequestType requestType, ArticleDocument articleDocument) {

        ArticleDocumentRequest request = switch (requestType) {
            case DELETE -> new ArticleDocumentRequest(requestType, null);
            case UPSERT -> new ArticleDocumentRequest(requestType, articleDocument);
        };

        kafkaTemplate.send(elasticsearchEventTopic, articleDocument.getId(), request);
    }
}
