public class Main {
    public static void main(String[] args) {
        // Создание списка
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
        
        // Добавление элементов
        list.add(5);
        list.add(2);
        list.add(8);
        list.add(1);
        
        // Вывод списка
        System.out.println("Original list:");
        list.forEach(v -> System.out.print(v + " "));
        System.out.println();
        
        // Вставка элемента
        list.insert(2, 3);
        System.out.println("\nAfter inserting 3 at index 2:");
        list.forEach(v -> System.out.print(v + " "));
        System.out.println();
        
        // Удаление элемента
        list.remove(1);
        System.out.println("\nAfter removing index 1:");
        list.forEach(v -> System.out.print(v + " "));
        System.out.println();
        
        // Сортировка
        list.sort();
        System.out.println("\nSorted list:");
        list.forEach(v -> System.out.print(v + " "));
    }
}