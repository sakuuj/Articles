package by.sakuuj.elasticsearch;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "by.sakuuj.elasticsearch.index-creator")
public class IndexCreatorProperties {

    private static final String INDEX_TO_JSON_FILE_SEPARATOR = "<->";

    private volatile List<Entry<String, String>> indexToJsonFilePairsParsed = null;
    private final Lock lock = new ReentrantLock();

    /**
     * <p>Pairs of type: 'index name'&lt;->'name of the file containing create index query'.</p>
     * <p>E.g. 'articles'&lt;->'createArticlesIndex.json'</p>
     */
    @Setter
    private List<String> indexToJsonFilePairs;

    public List<Entry<String, String>> getIndexToJsonFilePairs() {
        if (indexToJsonFilePairsParsed == null) {
            try {
                lock.lock();
                if (indexToJsonFilePairs == null) {
                    return List.of();
                }

                indexToJsonFilePairsParsed = indexToJsonFilePairs.stream()
                        .map(string -> {
                            String[] split = string.split("<->", 2);
                            if (split.length != 2) {
                                throw new RuntimeException("""
                                        Incorrect index to json file mapping is specified.
                                        Should be of pattern: indexName<->createQueryJsonFilePath.
                                        E.g. 'articles'<->'createArticlesIndex.json'""");
                            }
                            return Map.entry(split[0], split[1]);
                        })
                        .collect(Collectors.toCollection(ArrayList::new));

                return indexToJsonFilePairsParsed;

            } finally {
                lock.unlock();
            }
        }

        return indexToJsonFilePairsParsed;
    }
}
