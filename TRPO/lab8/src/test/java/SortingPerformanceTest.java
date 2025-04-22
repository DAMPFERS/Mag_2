import org.junit.jupiter.api.Test;
import java.util.Random;

public class SortingPerformanceTest {
    @Test
    void testSortingTime() {
        
        Random random = new Random();

        // Генерация 10 000 * j элементов
        for(int j = 1; j <= 256; j *= 2 ){
            UserType intType = new IntegerType();
            SinglyLinkedList list = new SinglyLinkedList(intType);

            for (int i = 0; i < 1000 * j; i++) {
                list.add(random.nextInt(1000));
            }
    
            long startTime = System.nanoTime();
            list.sort();
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;
    
            System.out.println("Время сортировки " + 10000*j + " элементов: " + durationMs + " мс");
            System.out.println("Количество узлов: " + list.getNodeCount());
            System.out.println("Память (примерно): " + (list.getNodeCount() * 32) + " байт");
        }
        
    }
}