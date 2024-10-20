package by.sakuuj.elasticsearch.json;

import by.sakuuj.elasticsearch.file.ToStringFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonContentExtractorImplTests {

    @Mock
    private ToStringFileReader toStringFileReader;

    @Mock
    private JsonValidator jsonValidator;

    @InjectMocks
    private JsonContentExtractorImpl jsonContentExtractorImpl;

    @Test
    void shouldThrowExceptionIfFileIsNotReadable() {

        String fileName = "unreadable";
        String expectedExceptionMsg = "NOT READABLE";

        when(toStringFileReader.read(eq(fileName), any()))
                .thenThrow(new RuntimeException(expectedExceptionMsg));

        assertThatThrownBy(() -> jsonContentExtractorImpl.extractJsonContent(fileName))
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

        assertThatThrownBy(() -> jsonContentExtractorImpl.extractJsonContent(fileName))
                .hasMessage(expectedExceptionMsg);
    }

    @Test
    void shouldReturnContentIfFileIsReadableAndContentIsValid() {

        String fileName = "readable";
        String fileContent = "valid content";

        when(toStringFileReader.read(eq(fileName), any()))
                .thenReturn(fileContent);
        doNothing().when(jsonValidator).validate(fileContent);

        assertThatNoException().isThrownBy(() -> jsonContentExtractorImpl.extractJsonContent(fileName));
    }
}
