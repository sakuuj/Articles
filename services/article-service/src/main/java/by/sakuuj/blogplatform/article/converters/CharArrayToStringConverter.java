package by.sakuuj.blogplatform.article.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class CharArrayToStringConverter implements Converter<char[], String> {
    @Override
    public String convert(char[] source) {
        return new String(source);
    }
}
