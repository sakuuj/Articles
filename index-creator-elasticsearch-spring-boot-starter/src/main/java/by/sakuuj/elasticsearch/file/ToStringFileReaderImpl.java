package by.sakuuj.elasticsearch.file;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;

public class ToStringFileReaderImpl implements ToStringFileReader {
    @Override
    public String read(String filePath, Charset charset) {
        try {
            return new ClassPathResource(filePath).getContentAsString(charset);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
