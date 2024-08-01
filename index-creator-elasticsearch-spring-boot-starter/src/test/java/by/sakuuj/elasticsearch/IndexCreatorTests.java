package by.sakuuj.elasticsearch;

import by.sakuuj.elasticsearch.http.client.IndexCreatorElasticsearchClient;
import by.sakuuj.elasticsearch.json.JsonContentExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IndexCreatorAutoConfiguration.class})
class IndexCreatorTests {

    @MockBean
    private IndexCreatorElasticsearchClient elasticsearchClient;

    @MockBean
    private JsonContentExtractor jsonContentExtractor;

    @Autowired
    private IndexCreator indexCreator;

    @Test
    void shouldNotDoAnythingIfAllIndexesAlreadyExist() {
        String firstIndexName = "first index";
        String firstIndexQueryFilePath = "first index json file path";

        String secondIndexName = "second index";
        String secondIndexQueryFilePath = "second index json file path";

        when(elasticsearchClient.indexExists(or(eq(firstIndexName), eq(secondIndexName))))
                .thenReturn(true);


        indexCreator.createIndexes(List.of(
                Map.entry(firstIndexName, firstIndexQueryFilePath),
                Map.entry(secondIndexName, secondIndexQueryFilePath)
        ));

        verify(elasticsearchClient).indexExists(firstIndexName);
        verify(elasticsearchClient).indexExists(secondIndexName);
        verifyNoMoreInteractions(elasticsearchClient);

        verifyNoInteractions(jsonContentExtractor);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldCreateOneIndex_WhenQueryIsExtractableAndOtherAlreadyExists(boolean secondIndexComesFirst) {
        // given
        String firstIndexName = "first index";
        String firstIndexQueryFilePath = "first index json file path";

        String secondIndexName = "second index";
        String secondIndexQueryFilePath = "second index json file path";
        String secondIndexFromFileQuery = "second index file content";

        List<Map.Entry<String, String>> indexToCreateQueryFileMappings =
                new ArrayList<>(List.of(
                        Map.entry(firstIndexName, firstIndexQueryFilePath),
                        Map.entry(secondIndexName, secondIndexQueryFilePath)
                ));

        if (secondIndexComesFirst) {
            Collections.reverse(indexToCreateQueryFileMappings);
        }


        when(elasticsearchClient.indexExists(firstIndexName))
                .thenReturn(true);

        when(elasticsearchClient.indexExists(secondIndexName))
                .thenReturn(false);
        when(jsonContentExtractor.extractJsonContent(secondIndexQueryFilePath))
                .thenReturn(secondIndexFromFileQuery);
        doNothing().when(elasticsearchClient).createIndex(secondIndexName, secondIndexFromFileQuery);

        // when
        indexCreator.createIndexes(indexToCreateQueryFileMappings);

        // then
        verify(elasticsearchClient).indexExists(firstIndexName);
        verify(elasticsearchClient).indexExists(secondIndexName);

        verify(elasticsearchClient).createIndex(secondIndexName, secondIndexFromFileQuery);
        verifyNoMoreInteractions(elasticsearchClient);

        verify(jsonContentExtractor).extractJsonContent(secondIndexQueryFilePath);
        verifyNoMoreInteractions(jsonContentExtractor);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldThrowException_OnIterationWhereQueryNotExtractable(boolean secondIndexComesFirst) {
        // given
        String firstIndexName = "first index";
        String firstIndexQueryFilePath = "first index json file path";

        String secondIndexName = "second index";
        String secondIndexQueryFilePath = "second index json file path";
        String secondIndexFromFileQuery = "second index file content";

        List<Map.Entry<String, String>> indexToCreateQueryFileMappings =
                new ArrayList<>(List.of(
                        Map.entry(firstIndexName, firstIndexQueryFilePath),
                        Map.entry(secondIndexName, secondIndexQueryFilePath)
                ));
        if (secondIndexComesFirst) {
            Collections.reverse(indexToCreateQueryFileMappings);

            when(jsonContentExtractor.extractJsonContent(secondIndexQueryFilePath))
                    .thenReturn(secondIndexFromFileQuery);
            doNothing().when(elasticsearchClient).createIndex(secondIndexName, secondIndexFromFileQuery);
        }
        
        when(elasticsearchClient.indexExists(firstIndexName))
                .thenReturn(false);
        when(elasticsearchClient.indexExists(secondIndexName))
                .thenReturn(false);

        when(jsonContentExtractor.extractJsonContent(firstIndexQueryFilePath))
                .thenThrow(new RuntimeException());
        
        // when, then
        assertThatThrownBy(() -> indexCreator.createIndexes(indexToCreateQueryFileMappings));
    }

    @Test
    void shouldCreateBothIndexes_IfBothQueriesExtractable_AndNeitherExists() {
        // given
        String firstIndexName = "first index";
        String firstIndexQueryFilePath = "first index json file path";
        String firstIndexFromFileQuery = "first index file content";

        String secondIndexName = "second index";
        String secondIndexQueryFilePath = "second index json file path";
        String secondIndexFromFileQuery = "second index file content";

        when(elasticsearchClient.indexExists(firstIndexName))
                .thenReturn(false);
        when(elasticsearchClient.indexExists(secondIndexName))
                .thenReturn(false);
        
        when(jsonContentExtractor.extractJsonContent(firstIndexQueryFilePath))
                .thenReturn(firstIndexFromFileQuery);
        when(jsonContentExtractor.extractJsonContent(secondIndexQueryFilePath))
                .thenReturn(secondIndexFromFileQuery);
        
        doNothing().when(elasticsearchClient).createIndex(firstIndexName, firstIndexFromFileQuery);
        doNothing().when(elasticsearchClient).createIndex(secondIndexName, secondIndexFromFileQuery);

        // when
        indexCreator.createIndexes(List.of(
                Map.entry(firstIndexName, firstIndexQueryFilePath),
                Map.entry(secondIndexName, secondIndexQueryFilePath)
        ));

        // then
        verify(elasticsearchClient).indexExists(firstIndexName);
        verify(elasticsearchClient).indexExists(secondIndexName);
        verify(elasticsearchClient).createIndex(firstIndexName, firstIndexFromFileQuery);
        verify(elasticsearchClient).createIndex(secondIndexName, secondIndexFromFileQuery);
        verifyNoMoreInteractions(elasticsearchClient);

        verify(jsonContentExtractor).extractJsonContent(firstIndexQueryFilePath);
        verify(jsonContentExtractor).extractJsonContent(secondIndexQueryFilePath);
        verifyNoMoreInteractions(jsonContentExtractor);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldThrowException_IfCreateIndexThrowsException(boolean secondIndexComesFirst) {
        // given
        String firstIndexName = "first index";
        String firstIndexQueryFilePath = "first index json file path";
        String firstIndexFromFileQuery = "first index file content";

        String secondIndexName = "second index";
        String secondIndexQueryFilePath = "second index json file path";
        String secondIndexFromFileQuery = "second index file content";


        List<Map.Entry<String, String>> indexToCreateQueryFileMappings =
                new ArrayList<>(List.of(
                        Map.entry(firstIndexName, firstIndexQueryFilePath),
                        Map.entry(secondIndexName, secondIndexQueryFilePath)
                ));

        if (secondIndexComesFirst) {
            Collections.reverse(indexToCreateQueryFileMappings);
        }
        
        when(elasticsearchClient.indexExists(firstIndexName))
                .thenReturn(false);
        when(elasticsearchClient.indexExists(secondIndexName))
                .thenReturn(false);

        when(jsonContentExtractor.extractJsonContent(firstIndexQueryFilePath))
                .thenReturn(firstIndexFromFileQuery);
        when(jsonContentExtractor.extractJsonContent(secondIndexQueryFilePath))
                .thenReturn(secondIndexFromFileQuery);

        doNothing().when(elasticsearchClient).createIndex(firstIndexName, firstIndexFromFileQuery);
        doThrow(new RuntimeException()).when(elasticsearchClient).createIndex(secondIndexName, secondIndexFromFileQuery);

        // when, then
        assertThatThrownBy(() -> indexCreator.createIndexes(indexToCreateQueryFileMappings));
    }
}
