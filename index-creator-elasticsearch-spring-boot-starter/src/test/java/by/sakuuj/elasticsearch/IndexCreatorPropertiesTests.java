package by.sakuuj.elasticsearch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class IndexCreatorPropertiesTests {

    @Test
    void shouldReturnEmptyListIfNoPropertiesProvided() {
        // given
        var indexCreatorProperties = new IndexCreatorProperties();

        // when
        List<Map.Entry<String, String>> actualPairs = indexCreatorProperties.getIndexToJsonFilePairs();

        // then
        assertThat(actualPairs).isEmpty();
    }

    @MethodSource
    @ParameterizedTest
    void shouldThrowExceptionOnMalformedMappings(List<String> listContainingMalformedMapping) {
        var indexCreatorProperties = new IndexCreatorProperties();
        indexCreatorProperties.setIndexToJsonFilePairs(
                listContainingMalformedMapping
        );

        assertThatThrownBy(indexCreatorProperties::getIndexToJsonFilePairs);
    }

    static List<Arguments> shouldThrowExceptionOnMalformedMappings() {
        return List.of(
                arguments(List.of("afajfklj")),
                arguments(List.of("somerh->fskj", "afkdls<-fallj"))
        );
    }

    @MethodSource
    @ParameterizedTest
    void shouldParseCorrectlyProvidedMappingsInTheSameOrder(
            List<String> listContainingMalformedMapping,
            List<Map.Entry<String, String>> expectedFromGetter
    ) {
        // given
        var indexCreatorProperties = new IndexCreatorProperties();
        indexCreatorProperties.setIndexToJsonFilePairs(
                listContainingMalformedMapping
        );

        // when
        List<Map.Entry<String, String>> actual = indexCreatorProperties.getIndexToJsonFilePairs();

        // then
        assertThat(actual).containsExactlyElementsOf(expectedFromGetter);
    }

    static List<Arguments> shouldParseCorrectlyProvidedMappingsInTheSameOrder() {

        List<String> providedMappings = new ArrayList<>(
                List.of("somerh<->zxc", "kafka<->mongo")
        );
        List<Map.Entry<String, String>> providedMappingsParsed = new ArrayList<>(
                List.of(entry("somerh", "zxc"), entry("kafka", "mongo"))
        );

        return List.of(
                arguments(
                        providedMappings,
                        providedMappingsParsed
                ),
                arguments(
                        providedMappings.reversed(),
                        providedMappingsParsed.reversed()
                ),
                arguments(
                        List.of("somer<->oops"),
                        List.of(entry("somer", "oops"))
                )
        );
    }
}
