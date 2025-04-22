// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;
// import java.util.Random;

// public class SinglyLinkedListTest {
//     private final UserType intType = new IntegerType();
//     private final UserType gpsType = new GPSCoordinateType();

//     // Тест 1: Сортировка пустого списка
//     @Test
//     void testSortEmptyList() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         System.out.println("Тест 1 - До сортировки: []");
//         list.sort();
//         System.out.println("Тест 1 - После сортировки: []");
//         assertEquals(0, list.size());
//         System.out.println("Тест 1 пройден: Сортировка пустого списка.");
//     }

//     // Тест 2: Сортировка списка из одного элемента
//     @Test
//     void testSortSingleElement() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         list.add(5);
//         System.out.println("Тест 2 - До сортировки: [5]");
//         list.sort();
//         System.out.println("Тест 2 - После сортировки: [5]");
//         assertEquals(5, list.get(0));
//         System.out.println("Тест 2 пройден: Сортировка списка из одного элемента.");
//     }

//     // Тест 3: Упорядоченный список (прямой порядок)
//     @Test
//     void testSortAlreadySorted() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         for (int i = 1; i <= 50; i++) {
//             list.add(i);
//         }
//         System.out.println("Тест 3 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 3 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = new Integer[50];
//         for (int i = 0; i < 50; i++) {
//             expected[i] = i + 1;
//         }
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 3 пройден: Упорядоченный список (прямой порядок).");
//     }

//     // Тест 4: Упорядоченный список (обратный порядок)
//     @Test
//     void testSortReverseSorted() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         for (int i = 50; i >= 1; i--) {
//             list.add(i);
//         }
//         System.out.println("Тест 4 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 4 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = new Integer[50];
//         for (int i = 0; i < 50; i++) {
//             expected[i] = i + 1;
//         }
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 4 пройден: Упорядоченный список (обратный порядок).");
//     }

//     // Тест 5: Неупорядоченный список
//     @Test
//     void testSortUnordered() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         Random rand = new Random();
//         Integer[] values = new Integer[50];
//         for (int i = 0; i < 50; i++) {
//             values[i] = rand.nextInt(100);
//             list.add(values[i]);
//         }
//         System.out.println("Тест 5 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 5 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = values.clone();
//         java.util.Arrays.sort(expected);
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 5 пройден: Неупорядоченный список.");
//     }

//     // Тест 6: Одинаковые элементы
//     @Test
//     void testSortAllEqual() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         for (int i = 0; i < 50; i++) {
//             list.add(5);
//         }
//         System.out.println("Тест 6 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 6 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = new Integer[50];
//         for (int i = 0; i < 50; i++) {
//             expected[i] = 5;
//         }
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 6 пройден: Одинаковые элементы.");
//     }

//     // Тест 7: Несколько групп повторений (GPS)
//     @Test
//     void testSortGPSWithGroups() {
//         SinglyLinkedList list = new SinglyLinkedList(gpsType);
//         for (int i = 0; i < 50; i++) {
//             list.add(gpsType.parseValue("55.751244;37.618423;2023-10-01T12:" + (i < 10 ? "0" : "") + i + ":00"));
//         }
//         System.out.println("Тест 7 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 7 - После сортировки: " + java.util.Arrays.toString(list.toArray()));

//         GPSCoordinate first = (GPSCoordinate) list.get(0);
//         assertEquals(55.751244, first.getLatitude(), 0.0001, "Широта первой координаты должна быть 55.751244");
//         System.out.println("Тест 7 пройден: Несколько групп повторений (GPS).");
//     }

//     // Тест 8: Экстремальное значение в начале
//     @Test
//     void testExtremeAtStart() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         list.add(-1000); // Минимальное значение
//         for (int i = 1; i < 50; i++) {
//             list.add(i);
//         }
//         System.out.println("Тест 8 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 8 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = new Integer[50];
//         expected[0] = -1000;
//         for (int i = 1; i < 50; i++) {
//             expected[i] = i;
//         }
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 8 пройден: Экстремальное значение в начале.");
//     }

//     // Тест 9: Экстремальное значение в конце
//     @Test
//     void testExtremeAtEnd() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         for (int i = 1; i < 50; i++) {
//             list.add(i);
//         }
//         list.add(1000); // Максимальное значение
//         System.out.println("Тест 9 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 9 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = new Integer[50];
//         for (int i = 1; i < 50; i++) {
//             expected[i - 1] = i;
//         }
//         expected[49] = 1000;
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 9 пройден: Экстремальное значение в конце.");
//     }

//     // Тест 10: Несколько экстремальных значений
//     @Test
//     void testMultipleExtremes() {
//         SinglyLinkedList list = new SinglyLinkedList(intType);
//         for (int i = 0; i < 25; i++) {
//             list.add(1000);
//             list.add(-1000);
//         }
//         System.out.println("Тест 10 - До сортировки: " + java.util.Arrays.toString(list.toArray()));
//         list.sort();
//         System.out.println("Тест 10 - После сортировки: " + java.util.Arrays.toString(list.toArray()));
//         Integer[] expected = new Integer[50];
//         for (int i = 0; i < 25; i++) {
//             expected[i] = -1000;
//             expected[i + 25] = 1000;
//         }
//         assertArrayEquals(expected, list.toArray());
//         System.out.println("Тест 10 пройден: Несколько экстремальных значений.");
//     }
// }
