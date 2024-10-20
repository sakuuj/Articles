package by.sakuuj.elasticsearch;

import by.sakuuj.elasticsearch.http.client.IndexCreatorElasticsearchClient;
import by.sakuuj.elasticsearch.json.JsonContentExtractor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class IndexCreatorImpl implements IndexCreator{

    private final JsonContentExtractor jsonContentExtractor;
    private final IndexCreatorElasticsearchClient elasticsearchClient;

    @Override
    public void createIndexes(List<Map.Entry<String, String>> indexToJsonFilePairs) {
        indexToJsonFilePairs.forEach(pair ->
        {
            String indexName = pair.getKey();
            if (elasticsearchClient.indexExists(indexName)) {
                return;
            }

            String jsonFile = pair.getValue();
            String jsonQuery = jsonContentExtractor.extractJsonContent(jsonFile);

            elasticsearchClient.createIndex(indexName, jsonQuery);
        });
    }
}