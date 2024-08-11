package by.sakuuj.elasticsearch.json;

import by.sakuuj.elasticsearch.IndexCreatorAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IndexCreatorAutoConfiguration.class})
class JsonValidatorImplIntegrationTests {

    @Autowired
    private JsonValidator jsonValidator;

    @Test
    void shouldAutowireJsonValidatorImpl() {

        assertThat(AopUtils.getTargetClass(jsonValidator))
                .isSameAs(JsonValidatorImpl.class);
    }

    @Test
    void shouldAcceptCorrectJsonContent() {

        String jsonContent = """
                {
                    "some field": "some value",
                    "list field": [1, 2, 3],
                    "float": 100.0,
                    "other obj" : {
                        "other val": 3E+2
                    }
                }
                """;

        assertThatNoException().isThrownBy(() -> jsonValidator.validate(jsonContent));
    }

    @MethodSource
    @ParameterizedTest
    void shouldRejectMalformedJsonContent(String malformedJsonContent) {

        assertThatThrownBy(() ->jsonValidator.validate(malformedJsonContent));
    }

    static List<String> shouldRejectMalformedJsonContent() {
        return List.of(
                """
                            "some field": "some value",
                            "list field": [1, 2, 3],
                            "float": 100.0,
                            "other obj" : {
                                "other val": 3E+2
                            }
                        }
                        """,
                """
                        {
                            "some field": "some value",
                            "list field": 1, 2, 3],
                            "float": 100.0,
                            "other obj" : {
                                "other val": 3E+2
                            }
                        }
                        """,
                """
                        {
                            "some field": some value",
                            "list field": [1, 2, 3],
                            "float": 100.0,
                            "other obj" : {
                                "other val": 3E+2
                            }
                        }
                        """,
                """
                        {
                            "some field": "some value",
                            "list field": [1, 2, 3],
                            "float": 100.0,
                            "other obj" : {
                                "other val": 3E+2E
                            }
                        }
                        """,
                """
                        {
                            "some field": "some value",
                            "list field": [1, 2, 3],
                            "float": 100.0,
                            "other obj" : {
                                "other val": 3E+2
                        }
                        """,
                """
                  {
                      "some field": "some value",
                      "list field": [1, 2, 3],
                      float: 100.0,
                      "other obj" : {
                          "other val": 3E+2
                      }
                  }
                  """
        );
    }
}
