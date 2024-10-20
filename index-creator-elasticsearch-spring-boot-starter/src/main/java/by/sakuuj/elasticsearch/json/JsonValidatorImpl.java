package by.sakuuj.elasticsearch.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonValidatorImpl implements JsonValidator{

    private final ObjectMapper objectMapper;

    @Override
    public void validate(String jsonContent) {
        try (JsonParser jsonParser = objectMapper.createParser(jsonContent)) {
            while(jsonParser.nextToken() != null){}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
