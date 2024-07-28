package by.sakuuj.blogplatform.article.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class FileUtils {

    public static String fileToString(String pathToFile) {
        var file = new ClassPathResource(pathToFile);
        try {
            return file.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
