package example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectToHistogramTest {

    @Test
    void should_collect_histogram() {
        Stream<String> stringStream = Stream.of("hello", "ee", "h");

        HashMap<String, Integer> expectMap = new HashMap<>();
        expectMap.put("h", 2);
        expectMap.put("e", 3);
        expectMap.put("l", 2);
        expectMap.put("o", 1);

        Map<String, Integer> actualMap = collectToHistogram(stringStream);

        assertEquals(expectMap.size(), actualMap.size());
        assertTrue(expectMap.keySet().containsAll(actualMap.keySet()));

        expectMap.keySet().forEach(key -> assertEquals(expectMap.get(key), actualMap.get(key)));
    }

    private static HashMap<String, Integer> collectToHistogram(Stream<String> stream) {
        return stream.collect(Collector.of(
                HashMap::new,
                (result, item) -> {
                    String[] keyArray = item.split("");
                    for (String key : keyArray) {
                        if (result.containsKey(key)) {
                            result.put(key, result.get(key) + 1);
                        } else {
                            result.put(key, 1);
                        }
                    }
                },
                (finallyResult, resultToMerge) -> {
                    resultToMerge.forEach((key, value) -> {
                        if (finallyResult.containsKey(key)) {
                            finallyResult.put(key, finallyResult.get(key) + value);
                        } else {
                            finallyResult.put(key, value);
                        }
                    });
                    return finallyResult;
                }
        ));
    }
}
