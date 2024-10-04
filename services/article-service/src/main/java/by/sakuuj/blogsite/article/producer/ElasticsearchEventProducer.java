package by.sakuuj.blogsite.article.producer;


import by.sakuuj.blogsite.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.entity.elasticsearch.ArticleDocumentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@RequiredArgsConstructor
public class ElasticsearchEventProducer {

    @Value("${elasticsearchEventTopic}")
    private String elasticsearchEventTopic;

    private final KafkaTemplate<UUID, ArticleDocumentRequest> kafkaTemplate;

    public void produce(ArticleDocumentRequest.RequestType requestType, ArticleDocument articleDocument) {

        ArticleDocumentRequest request = new ArticleDocumentRequest(requestType, articleDocument);
        kafkaTemplate.send(elasticsearchEventTopic, articleDocument.getId(), request);
    }
}
