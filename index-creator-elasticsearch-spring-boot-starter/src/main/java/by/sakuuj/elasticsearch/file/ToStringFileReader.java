package by.sakuuj.elasticsearch.file;

import java.nio.charset.Charset;

public interface ToStringFileReader {
    String read(String filePath, Charset charset);
}
