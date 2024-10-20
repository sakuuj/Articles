package by.sakuuj.articles.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidUtilsTests {

    @Test
    void shouldRemoveHyphensFromUuid() {

        // given
        UUID uuid = UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd");
        String expected = "12123febc6e94fedace81f21eb3f90bd";

        // when
        String actual = UuidUtils.removeHyphens(uuid);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldRestoreHyphens() {

        // given
        String uuidWithoutHyphens = "12123febc6e94fedace81f21eb3f90bd";
        UUID expected = UUID.fromString("12123feb-c6e9-4fed-ace8-1f21eb3f90bd");

        // when
        UUID actual = UuidUtils.restoreHyphens(uuidWithoutHyphens);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
