package by.sakuuj.blogplatform.article.database.schema;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "by.sakuuj.elasticsearch")
public class ElasticsearchIndexCreatorProperties {

    private Map<String, String> indexToJsonFileMappings;
}
