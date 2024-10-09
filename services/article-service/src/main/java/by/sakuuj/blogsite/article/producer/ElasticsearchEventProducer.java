package by.sakuuj.blogsite.article.producer;


import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ElasticsearchEventProducer {

    @Value("${by.sakuuj.blogsite.article.kafka.topic-name}")
    private String elasticsearchEventTopic;

    private final KafkaTemplate<UUID, ArticleDocumentRequest> kafkaTemplate;

    public void produce(ArticleDocumentRequest.RequestType requestType, ArticleDocument articleDocument) {

        ArticleDocumentRequest request = new ArticleDocumentRequest(requestType, articleDocument);
        kafkaTemplate.send(elasticsearchEventTopic, articleDocument.getId(), request);
    }
}
