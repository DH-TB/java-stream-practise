package example;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static org.junit.jupiter.api.Assertions.*;

class CollectPractiseTest {
    @Test
    void should_collect_to_set() {
        Stream<String> stringStream = Stream.of("Hello", "Hello", "World");

        Set<String> actual = collectToSet(stringStream);
        Set<String> expect = new HashSet<>(Arrays.asList("Hello", "World"));

        assertIterableEquals(expect, actual);
    }

    private static Set<String> collectToSet(Stream<String> stream) {
        return stream.parallel().collect(Collector.of(
                () -> new HashSet<>(),
                (result, item) -> result.add(item),
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }));
    }


    @Test
    void should_collect_double_to_sum() {
        Stream<Double> doubleStream = Stream.of(1.1, 2.2, 3.3, 4.4);
        Double actual = collectDoubleToSum(doubleStream);
        Double expect = 11.0;

        assertEquals(expect, actual);
    }


    private static Double collectDoubleToSum(Stream<Double> stream) {
        return stream.parallel().collect(Collector.of(
                () -> new Double[1],
                (result, item) -> result[0] += item,
                (left, right) -> {
                    left[0] += right[0];
                    return left;
                }
        ))[0];
    }


    @Test
    void should_collect_to_sum() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4).parallel();
        Integer actual = collectToSum(integerStream);
        Integer expect = 10;

        assertEquals(expect, actual);
    }

    private static Integer collectToSum(Stream<Integer> stream) {
        return stream.collect(Collector.of(
                () -> new int[1],
                (result, item) -> result[0] += item,
                (left, right) -> {
                    int sum = left[0] + right[0];
                    return new int[]{sum};
                },
                (array) -> array[0]
        ));
    }


    @Test
    void should() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7).parallel();
        Stream<Stream<Integer>> splitNumberByOdevity = splitNumberByOdevity(integerStream);

        Stream[] array = splitNumberByOdevity.toArray(Stream[]::new);

        assertArrayEquals(new Integer[]{2, 4, 6}, array[0].toArray(Integer[]::new));
        assertArrayEquals(new Integer[]{1, 3, 5, 7}, array[1].toArray(Integer[]::new));

    }

    Stream<Stream<Integer>> splitNumberByOdevity(Stream<Integer> integerStream) {
        HashMap<Integer, ArrayList<Integer>> collect = integerStream.collect(Collector.of(
                HashMap::new,
                (result, item) -> {
                    int key = item % 2;
                    if (result.containsKey(key)) {
                        ArrayList<Integer> arrayList = result.get(key);
                        arrayList.add(item);
                    } else {
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(item);
                        result.put(key, arrayList);
                    }
                },
                (finallyResult, resultToMerge) -> {
                    resultToMerge.forEach((key, value) -> {
                        if (finallyResult.containsKey(key)) {
                            finallyResult.get(key).addAll(value);
                        } else {
                            finallyResult.put(key, value);
                        }
                    });
                    return finallyResult;
                }
        ));
        return collect.keySet().stream().map(key -> collect.get(key).stream());
    }

    @Test
    void should_calculate_character_count() {
        Stream<String> stringStream = Stream.of("Hello", "hello", "e").parallel();
        Optional.of(1).orElseGet();
        Map<String, Integer> actualMap = collectToHistogram(stringStream);
        HashMap<String, Integer> expectMap = new HashMap<>();
        expectMap.put("H", 1);
        expectMap.put("e", 3);
        expectMap.put("h", 1);
        expectMap.put("l", 4);
        expectMap.put("o", 2);


        expectMap.keySet().stream().forEach(key -> {
            assertEquals(expectMap.get(key), actualMap.get(key));
        });

        assertEquals(expectMap.size(), actualMap.size());
        assertTrue(actualMap.keySet().containsAll(expectMap.keySet()));

    }

    static Map<String, Integer> collectToHistogram(Stream<String> stream) {
        HashMap<String, Integer> collect = stream.collect(Collector.of(
                        () -> new HashMap<>(),
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
        return collect;
    }
}

//https://codereview.stackexchange.com/questions/133150/standard-test-methods-for-checking-values-in-lists-maps-etc
