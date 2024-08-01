package by.sakuuj.elasticsearch.http.client;

public interface IndexCreatorElasticsearchClient {
    boolean indexExists(String indexName);
    void createIndex(String indexName, String jsonQuery);
}
