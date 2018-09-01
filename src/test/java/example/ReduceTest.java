package example;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReduceTest {

    @Test
    void should() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3);
        int sum = sum(integerStream);
        assertEquals(6, sum);
    }

    private Integer sum(Stream<Integer> stream) {
        return stream.reduce(0, (num1, num2) -> num1 + num2);
    }


    @Test
    void should_groupBy() {
        Stream<String> stringStream = Stream.of("hello", "hi");

        Map<String, Integer> actualMap = groupBy(stringStream);

        HashMap<String, Integer> expectMap = new HashMap<>();
        expectMap.put("h", 2);
        expectMap.put("e", 1);
        expectMap.put("l", 2);
        expectMap.put("o", 1);
        expectMap.put("i", 1);

        assertEquals(expectMap.size(), actualMap.size());
        assertTrue(expectMap.keySet().containsAll(actualMap.keySet()));

        expectMap.keySet().forEach(key -> assertEquals(expectMap.get(key), actualMap.get(key)));

    }

    private Map<String, Integer> groupBy(Stream<String> stream) {
        Stream<String> characterStream = stream.flatMap(s -> s.chars().mapToObj(i -> (char) i)).map(s -> s.toString().toLowerCase());
        return characterStream.collect(Collectors.groupingBy(character -> character.toLowerCase(),
                Collectors.reducing(0, character -> 1, (count1, count2) -> count1 + count2)));
    }

    @Test
    void should_test_group(){
        Map<String, List<String>> actualMap = groupBy1(Arrays.asList("A", "B", "A"), (item) -> item.toLowerCase(), () -> new HashMap<>());
        HashMap<String, List<String>> expectMap = new HashMap<>();
        expectMap.put("a", Arrays.asList("A", "A"));
        expectMap.put("b", Collections.singletonList("B"));

        assertEquals(expectMap.size(), actualMap.size());
        assertTrue(expectMap.keySet().containsAll(actualMap.keySet()));


        expectMap.forEach((key,value) -> assertEquals(expectMap.get(key), actualMap.get(key)));

    }

    private <T, K> Map<K, List<T>> groupBy1(Iterable<T> sequence, Function<T, K> keyGenerator, Supplier<Map<K, List<T>>> supplier) {
        Map<K, List<T>> resultMap = supplier.get();

        for (T item : sequence) {

            K key = keyGenerator.apply(item);
            if (resultMap.containsKey(key)) {
                List<T> list = resultMap.get(key);
                list.add(item);
            } else {
                List<T> arrayList = new ArrayList<>();
                arrayList.add(item);
                resultMap.put(key, arrayList);
            }
        }
        return resultMap;
    }
}


