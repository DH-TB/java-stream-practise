package example;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectTest {

    @Test
    void should_copy_array_to_list() {
        ArrayList<Integer> listSource = new ArrayList<>();
        listSource.add(1);
        listSource.add(2);

        List<Integer> collectArray = collectArray(listSource, () -> new ArrayList<Integer>(), (result, item) -> result.add(item));

        assertIterableEquals(collectArray, listSource);
    }

    private List<Integer> collectArray(ArrayList<Integer> collectArray, Supplier<List<Integer>> listSupplier, BiConsumer<List<Integer>, Integer> consumer) {
        List<Integer> resultList = listSupplier.get();
        for (Integer item : collectArray) {
            consumer.accept(resultList, item);
        }
//        resultList.addAll(collectArray);

        return resultList;
    }

    @Test
    void should() {
        List<String> list = Arrays.asList("hello", "world", "1", "2");

        ArrayList<Object> dest1 = new ArrayList<>();
        ArrayList<Object> dest2 = new ArrayList<>();
        ArrayList<Object> dest3 = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            dest1.add(list.get(i));
        }

        for (int i = 2; i < list.size(); i++) {
            dest2.add(list.get(i));
        }

        for (int i = 0; i < dest1.size(); i++) {
            dest3.add(dest1.get(i));
        }

        for (int i = 0; i < dest2.size(); i++) {
            dest3.add(dest2.get(i));
        }
    }


    @Test
    void should_collect_histogram_1() {
        Stream<String> stringStream = Stream.of("hello", "ee", "h").parallel();

        HashMap<String, Integer> expectMap = new HashMap<>();
        expectMap.put("h", 2);
        expectMap.put("e", 3);
        expectMap.put("l", 2);
        expectMap.put("o", 1);

        Map<String, Integer> actualMap = collectToHistogram(stringStream);

        assertEquals(expectMap.size(), actualMap.size());
        assertTrue(expectMap.keySet().containsAll(actualMap.keySet()));

        expectMap.keySet().forEach(key -> assertEquals(expectMap.get(key), actualMap.get(key), "key:" + key));
    }

    private static HashMap<String, Integer> collectToHistogram(Stream<String> stream) {
        Stream<Character> characterStream = stream.flatMap(string -> string.chars().mapToObj(i -> (char) i));

        return characterStream.collect(Collector.of(
                HashMap::new,
                (result, item) -> {
                    String key = item.toString();
                    if (result.containsKey(key)) {
                        result.put(key, result.get(key) + 1);
                    } else {
                        result.put(key, 1);
                    }
                },
                (finallyResult, resultToMerge) -> {
                    resultToMerge.forEach((String key, Integer value) -> {
                        if (finallyResult.containsKey(key)) {
                            finallyResult.put(key, finallyResult.get(key) + value);
                        } else {
                            finallyResult.putAll(resultToMerge);
                        }
                    });
                    return finallyResult;
                }
        ));
    }


    @Test
    void should_collect_reduce() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3).parallel();
        Integer integer = collectToReduce(integerStream, 0, (result, item) -> result + item, (result, item) -> result + item);
        System.out.println(integer);

    }

    private <T> T collectToReduce(Stream<T> stream, T identity, BiFunction<T, ? super T, T> accumulator, BinaryOperator<T> combiner) {
        class ValueHolder {
            private T value;
        }
        return stream.collect(Collector.of(
                () -> {
                    ValueHolder valueHolder = new ValueHolder();
                    valueHolder.value = identity;
                    return valueHolder;
                },
                (result, item) -> result.value = accumulator.apply(result.value, item),
                (finallyResult, resultToMerge) -> {
                    ValueHolder valueHolder = new ValueHolder();
                    valueHolder.value = combiner.apply(finallyResult.value, resultToMerge.value);
                    return valueHolder;
                },
                (valueHolder) -> valueHolder.value
        ));
    }

    private <U> U reduce(Stream<U> stream, U identity, BiFunction<U, U, U> accumulator, BinaryOperator<U> combiner) {
        U reduce = stream.reduce(
                identity,
                (result, item) -> {
                    U apply = accumulator.apply(result, item);
                    return apply;
                },
                (finallyResult, resultToMerge) -> {
                    U apply = combiner.apply(finallyResult, resultToMerge);
                    return apply;
                }
        );
        return reduce;
    }
}
