package by.sakuuj.elasticsearch.file;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ToStringFileReaderImplIntegrationTests {

    private final ToStringFileReaderImpl toStringFileReader = new ToStringFileReaderImpl();

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    void shouldThrowExceptionIfFileDoesNotExist() {

        String nonExistentFileName = "non-existent-file-name";
        assertThat(Path.of(nonExistentFileName)).doesNotExist();

        assertThatThrownBy(() -> toStringFileReader.read(nonExistentFileName, CHARSET));
    }

    @Test
    void shouldReadFileIfExists() {

        String expectedContent = new String("some content".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        String existingFileName = "test-files/file-to-read.txt";
        assertThat(new ClassPathResource(existingFileName).exists()).isTrue();

        String actualContent = toStringFileReader.read(existingFileName, CHARSET);

        assertThat(actualContent).isEqualTo(expectedContent);
    }

}
