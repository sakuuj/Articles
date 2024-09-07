package by.sakuuj.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@UtilityClass
public class LocalDateTimeComparator {

    private static final Comparator<LocalDateTime> localDateTimeComparator = (o1, o2) ->
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");

        String o1Formatted = o1.format(dateTimeFormatter);
        String o2Formatted = o2.format(dateTimeFormatter);
        return CharSequence.compare(o1Formatted, o2Formatted);
    };

    public static Comparator<LocalDateTime> getInstance() {
        return localDateTimeComparator;
    }
}
