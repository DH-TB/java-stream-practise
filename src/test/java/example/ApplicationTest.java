package example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ApplicationTest {

    @Test
    void should_filter_length_greater_than_4() {
        Stream<String> stringStream = Stream.of("a", "quick", "brown", "fox", "jumps", "over");

        String[] filterArray = stringStream.filter(string -> string.length() > 4).toArray(String[]::new);
        String[] expect = {"quick", "brown", "jumps"};

        assertArrayEquals(expect, filterArray);
    }

    @Test
    void should_uppercase() {
        Stream<String> stringStream = Stream.of("a", "quick", "brown", "fox", "jumps", "over");
        String[] except = {"QUICK", "BROWN", "JUMPS"};

        String[] mapArray = stringStream.filter(s -> s.length() > 4).map(String::toUpperCase).toArray(String[]::new);

        assertArrayEquals(except, mapArray);
    }

    @Test
    void should_get_each_char_as_a_character_array() {
        Stream<String> stringStream = Stream.of("a", "quick", "brown", "fox", "jumps", "over");
        Character[] except = {'Q', 'U', 'I', 'C', 'K', 'B', 'R', 'O', 'W', 'N', 'J', 'U', 'M', 'P', 'S'};

        Character[] characters = stringStream
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase)
                .map(item -> item.chars().mapToObj(i -> (char) i))
                .flatMap(stream -> stream)
                .toArray(Character[]::new);


        assertArrayEquals(except, characters);
    }


    Stream<Character> toStream(String text) {
        return text.chars().mapToObj(i -> (char) i);
    }

    Stream<String> cartesianProduct(Stream<Character> left, Stream<Character> right) {
        Character[] characters = right.toArray(Character[]::new);

        return left.map(a -> Arrays.stream(characters).map(b -> String.valueOf(a) + b)).flatMap(stream -> stream);
    }

    @Test
    void should_flatMap() {
        Stream<Character> seq1 = Stream.of('A', 'B', 'C');
        Stream<Character> seq2 = Stream.of('a', 'b');
        String[] actual = cartesianProduct(seq1, seq2).toArray(String[]::new);

        String[] except = new String[]{"Aa", "Ab", "Ba", "Bb", "Ca", "Cb"};
        assertArrayEquals(except, actual);
    }

    @Test
    void should_flatMap_left_join() {
        Stream<Employee> employeeStream = Stream.of(
                new Employee(1, "Obama"),
                new Employee(2, "Clinton")
        );

        Stream<Order> orderStream = Stream.of(
                new Order(1, "Coca Cola"),
                new Order(1, "Nike"),
                new Order(2, "Plane"),
                new Order(2, "Toy"),
                new Order(2, "Pepsi")
        );

        Stream<String> combination = combination(employeeStream, orderStream);
        String[] strings = combination.toArray(String[]::new);

        String[] except = {"Obama - Coca Cola", "Obama - Nike", "Clinton - Plane", "Clinton - Toy", "Clinton - Pepsi"};
        assertArrayEquals(except, strings);
    }

    Stream<String> combination(Stream<Employee> employeeStream, Stream<Order> orderStream) {
        Order[] orders = orderStream.toArray(Order[]::new);

        return employeeStream.map(employee -> Arrays.stream(orders)
                .filter(order -> employee.getId() == order.getEmployeeId())
                .map(order -> employee.getName() + " - " + order.getProductName()))
                .flatMap(stream -> stream);
    }
}


//lazy
//flatMap + 铺平 笛卡尔积 leftJoin
//collect
//reduce
