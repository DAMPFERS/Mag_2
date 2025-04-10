// import java.util.NoSuchElementException;

// Интерфейс для итератора
interface Callback<T> {
    void toDo(T v);
}


// Класс узла списка
class Node<T> {
    T data;
    Node<T> next;
    
    Node(T data) {
        this.data = data;
        this.next = null;
    }
}


// Основной класс списка
public class SinglyLinkedList<T extends Comparable<T>> {
    private Node<T> head;
    private int size;
    
    public SinglyLinkedList() {
        head = null;
        size = 0;
    }
    

    // Добавление в конец
    public void add(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    // Получение элемента по индексу
    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    // Вставка по индексу
    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        
        Node<T> newNode = new Node<>(value);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node<T> prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            newNode.next = prev.next;
            prev.next = newNode;
        }
        size++;
    }
    
    // Удаление по индексу
    public void remove(int index) {
        checkIndex(index);
        if (index == 0) {
            head = head.next;
        } else {
            Node<T> prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            prev.next = prev.next.next;
        }
        size--;
    }
    
    // Итератор forEach
    public void forEach(Callback<T> callback) {
        Node<T> current = head;
        while (current != null) {
            callback.toDo(current.data);
            current = current.next;
        }
    }
    
    // Сортировка слиянием
    public void sort() {
        head = mergeSort(head);
    }
    
    private Node<T> mergeSort(Node<T> head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        Node<T> middle = getMiddle(head);
        Node<T> nextOfMiddle = middle.next;
        middle.next = null;
        
        Node<T> left = mergeSort(head);
        Node<T> right = mergeSort(nextOfMiddle);
        
        return merge(left, right);
    }
    
    private Node<T> merge(Node<T> left, Node<T> right) {
        Node<T> result;
        if (left.data.compareTo(right.data) <= 0) {
            result = left;
            left = left.next;
        } else {
            result = right;
            right = right.next;
        }
        Node<T> temp = result;
        
        while (left != null && right != null) {
            if (left.data.compareTo(right.data) <= 0) {
                temp.next = left;
                left = left.next;
            } else {
                temp.next = right;
                right = right.next;
            }
            temp = temp.next;
        }
        
        temp.next = (left != null) ? left : right;
        return result;
    }
    
    private Node<T> getMiddle(Node<T> head) {
        if (head == null) return null;
        Node<T> slow = head;
        Node<T> fast = head;
        
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }
    
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
    
    public int size() {
        return size;
    }
}