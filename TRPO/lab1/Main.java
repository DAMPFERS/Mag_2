import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) {
        // Создание объектов
        GPSCoordinate point1 = new GPSCoordinate(
            55.751244, 
            37.618423, 
            LocalDateTime.parse("2023-10-01T12:00:00")
        );
        
        GPSCoordinate point2 = new GPSCoordinate(
            59.934280, 
            30.335098, 
            LocalDateTime.parse("2023-10-01T12:30:00")
        );

        // Тестирование методов
        System.out.println("Point 1: " + point1);
        System.out.println("Point 2: " + point2);
        System.out.printf("Distance between points: %.2f km\n", point1.distanceTo(point2));

        // Тестирование сериализации
        try {
            // Текстовая сериализация
            String textData = point1.toString();
            GPSCoordinate restoredFromText = GPSCoordinate.fromString(textData);
            System.out.println("Restored from text: " + restoredFromText);

            // Бинарная сериализация
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(byteStream);
            oos.writeObject(point2);
            oos.close();
            
            ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(byteStream.toByteArray())
            );
            GPSCoordinate restoredFromBinary = (GPSCoordinate) ois.readObject();
            System.out.println("Restored from binary: " + restoredFromBinary);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Тестирование исключений
        try {
            new GPSCoordinate(100.0, 200.0, LocalDateTime.now());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}