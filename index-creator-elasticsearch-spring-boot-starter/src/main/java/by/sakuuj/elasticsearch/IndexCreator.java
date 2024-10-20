package by.sakuuj.elasticsearch;

import java.util.List;
import java.util.Map.Entry;

public interface IndexCreator {
    void createIndexes(List<Entry<String, String>> indexToCreateQueryFilePairs);
}
