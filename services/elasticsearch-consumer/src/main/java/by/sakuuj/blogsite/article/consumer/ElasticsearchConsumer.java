package by.sakuuj.blogsite.article.consumer;

import by.sakuuj.blogsite.article.dto.ArticleDocumentRequest;
import by.sakuuj.blogsite.article.entity.elasticsearch.ArticleDocument;
import by.sakuuj.blogsite.article.exceptions.RecordKeyIsAbsentException;
import by.sakuuj.blogsite.article.repository.elasticsearch.ArticleDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ElasticsearchConsumer {

    public static final String TOPIC_NAME = "articles";
    public static final String CONSUMED_TYPE_NAME = "articleDocReq";

    private final ArticleDocumentRepository articleDocumentRepository;


    @KafkaListener(topics = TOPIC_NAME, groupId = "1", concurrency = "3")
    public void consumeArticle(ConsumerRecord<UUID, ArticleDocumentRequest> consumerRecord) {

        UUID id = consumerRecord.key();
        if (id == null) {
            throw new RecordKeyIsAbsentException();
        }

        ArticleDocumentRequest request = consumerRecord.value();

        ArticleDocumentRequest.RequestType requestType = request.type();
        ArticleDocument article = request.articleDocument();

        switch (requestType) {
            case UPSERT -> articleDocumentRepository.save(article);
            case DELETE -> articleDocumentRepository.deleteById(id);
        }
    }
}
