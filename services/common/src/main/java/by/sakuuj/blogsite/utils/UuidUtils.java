package by.sakuuj.blogsite.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UuidUtils {

    public static String removeHyphens(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static UUID restoreHyphens(String uuidWithoutHyphens) {

        StringBuilder sb = new StringBuilder(uuidWithoutHyphens);
        sb.insert(8, "-");
        sb.insert(12 + 1, "-");
        sb.insert(16 + 2, "-");
        sb.insert(20 + 3, "-");

        return UUID.fromString(sb.toString());
    }
}
