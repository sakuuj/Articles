package by.sakuuj.elasticsearch.json;

import by.sakuuj.elasticsearch.file.ToStringFileReader;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JsonContentExtractorImpl implements JsonContentExtractor {

    private final JsonValidator jsonValidator;
    private final ToStringFileReader toStringFileReader;

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public String extractJsonContent(String filePath) {
        String jsonContent = toStringFileReader.read(filePath, CHARSET);
        jsonValidator.validate(jsonContent);
        return jsonContent;
    }
}
