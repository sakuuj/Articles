package by.sakuuj.blogplatform.article.utils;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

import java.util.Arrays;

@UtilityClass
public class TestUtils {

    public static final RecursiveComparisonConfiguration COMPARISON_FOR_CHAR_ARRAY = RecursiveComparisonConfiguration.builder()
            .withComparatorForType(Arrays::compare, char[].class)
            .build();
}
