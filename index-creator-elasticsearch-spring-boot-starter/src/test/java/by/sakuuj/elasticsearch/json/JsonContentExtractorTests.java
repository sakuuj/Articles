package by.sakuuj.elasticsearch.json;

import by.sakuuj.elasticsearch.IndexCreatorAutoConfiguration;
import by.sakuuj.elasticsearch.file.ToStringFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IndexCreatorAutoConfiguration.class})
class JsonContentExtractorTests {

    @MockBean
    private ToStringFileReader toStringFileReader;

    @MockBean
    private JsonValidator jsonValidator;

    @Autowired
    private JsonContentExtractor jsonContentExtractor;

    @Test
    void shouldThrowExceptionIfFileIsNotReadable() {

        String fileName = "unreadable";
        String expectedExceptionMsg = "NOT READABLE";

        when(toStringFileReader.read(eq(fileName), any()))
                .thenThrow(new RuntimeException(expectedExceptionMsg));

        assertThatThrownBy(() -> jsonContentExtractor.extractJsonContent(fileName))
                .hasMessage(expectedExceptionMsg);
    }

    @Test
    void shouldThrowExceptionIfFileIsReadableButNotValid() {
        String fileName = "readable";
        String fileContent = "invalid content";

        when(toStringFileReader.read(eq(fileName), any()))
                .thenReturn(fileContent);

        String expectedExceptionMsg = "INVALID";
        doThrow(new RuntimeException(expectedExceptionMsg)).when(jsonValidator).validate(fileContent);

        assertThatThrownBy(() -> jsonContentExtractor.extractJsonContent(fileName))
                .hasMessage(expectedExceptionMsg);
    }

    @Test
    void shouldReturnContentIfFileIsReadableAndContentIsValid() {

        String fileName = "readable";
        String fileContent = "valid content";

        when(toStringFileReader.read(eq(fileName), any()))
                .thenReturn(fileContent);
        doNothing().when(jsonValidator).validate(fileContent);

        assertThatNoException().isThrownBy(() -> jsonContentExtractor.extractJsonContent(fileName));
    }
}
