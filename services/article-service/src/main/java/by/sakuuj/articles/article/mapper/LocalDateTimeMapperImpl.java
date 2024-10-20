package by.sakuuj.articles.article.mapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LocalDateTimeMapperImpl implements LocalDateTimeMapper {

    private final FormattingConversionService formattingConversionService;

    public LocalDateTimeMapperImpl(@Qualifier("conversionService")
                                   FormattingConversionService formattingConversionService) {

        this.formattingConversionService = formattingConversionService;
    }

    @Override
    public String toStringDateTime(LocalDateTime localDateTime) {

        return formattingConversionService.convert(localDateTime, String.class);
    }

    @Override
    public LocalDateTime toJavaDateTime(String localDateTime) {

        return formattingConversionService.convert(localDateTime, LocalDateTime.class);
    }
}
