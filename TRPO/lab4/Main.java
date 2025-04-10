public class Main {
    public static void main(String[] args) {
        TypeFactory factory = new TypeFactory();
        
        // Тестирование с целыми числами
        UserType intType = factory.getType("Integer");
        SinglyLinkedList intList = new SinglyLinkedList(intType);
        
        intList.add(intType.parseValue("5"));
        intList.add(intType.parseValue("2"));
        intList.add(intType.parseValue("8"));
        
        System.out.println("Original Integer List:");
        intList.forEach(data -> System.out.print(data + " "));
        
        intList.sort();
        System.out.println("\nSorted Integer List:");
        intList.forEach(data -> System.out.print(data + " "));

        // Тестирование с GPS-координатами
        UserType gpsType = factory.getType("GPSCoordinate");
        SinglyLinkedList gpsList = new SinglyLinkedList(gpsType);
        
        gpsList.add(gpsType.parseValue("55.751244;37.618423;2023-10-01T12:00:00"));
        gpsList.add(gpsType.parseValue("59.934280;30.335098;2023-10-01T12:30:00"));
        gpsList.add(gpsType.parseValue("51.5074;-0.1278;2023-10-01T13:00:00"));
        
        System.out.println("\n\nOriginal GPS List:");
        gpsList.forEach(data -> System.out.println(data));
        
        gpsList.sort();
        System.out.println("\nSorted GPS List (by latitude):");
        gpsList.forEach(data -> System.out.println(data));
    }
}