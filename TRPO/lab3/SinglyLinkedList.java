public class SinglyLinkedList {
    private Node head;
    private int size;
    private final UserType userType;
    private final Comparator comparator;

    private static class Node {
        Object data;
        Node next;
        
        Node(Object data) {
            this.data = data;
            this.next = null;
        }
    }

    public SinglyLinkedList(UserType userType) {
        this.userType = userType;
        this.comparator = userType.getTypeComparator();
        this.head = null;
        this.size = 0;
    }

    public void add(Object value) {
        Node newNode = new Node(userType.clone(value));
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public Object get(int index) {
        checkIndex(index);
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public void insert(int index, Object value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        
        Node newNode = new Node(userType.clone(value));
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            newNode.next = prev.next;
            prev.next = newNode;
        }
        size++;
    }

    public void remove(int index) {
        checkIndex(index);
        if (index == 0) {
            head = head.next;
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            prev.next = prev.next.next;
        }
        size--;
    }

    public void forEach(Callback callback) {
        Node current = head;
        while (current != null) {
            callback.toDo(current.data);
            current = current.next;
        }
    }

    public void sort() {
        head = mergeSort(head);
    }

    private Node mergeSort(Node head) {
        if (head == null || head.next == null) return head;

        Node middle = getMiddle(head);
        Node nextOfMiddle = middle.next;
        middle.next = null;

        Node left = mergeSort(head);
        Node right = mergeSort(nextOfMiddle);

        return merge(left, right);
    }

    private Node merge(Node left, Node right) {
        Node result;
        if (comparator.compare(left.data, right.data) <= 0) {
            result = left;
            left = left.next;
        } else {
            result = right;
            right = right.next;
        }
        Node temp = result;

        while (left != null && right != null) {
            if (comparator.compare(left.data, right.data) <= 0) {
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

    private Node getMiddle(Node head) {
        if (head == null) return null;
        Node slow = head;
        Node fast = head;

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

interface Callback {
    void toDo(Object data);
}