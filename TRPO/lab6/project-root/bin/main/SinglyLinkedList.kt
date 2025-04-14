import java.io.Serializable

class SinglyLinkedList(val userType: UserType) : Serializable {
    private data class Node(var data: Any, var next: Node? = null)
    private var head: Node? = null
    private var size = 0

    // Добавление элемента
    fun add(value: Any) {
        val cloned = userType.clone(value)
        val newNode = Node(cloned)
        if (head == null) {
            head = newNode
        } else {
            var current = head
            while (current?.next != null) {
                current = current.next
            }
            current?.next = newNode
        }
        size++
    }

    // Получение элемента по индексу
    fun get(index: Int): Any? {
        require(index in 0 until size) { "Индекс $index вне диапазона" }
        var current = head
        repeat(index) { current = current?.next }
        return current?.data
    }

    // Удаление элемента
    fun remove(index: Int): Boolean {
        if (index !in 0 until size) return false
        if (index == 0) {
            head = head?.next
        } else {
            var prev = head
            repeat(index - 1) { prev = prev?.next }
            prev?.next = prev?.next?.next
        }
        size--
        return true
    }

    // Сортировка (слиянием)
    fun sort() {
        head = mergeSort(head)
    }

    private fun mergeSort(head: Node?): Node? {
        if (head?.next == null) return head
        val middle = getMiddle(head)
        val nextToMiddle = middle?.next
        middle?.next = null
        return merge(mergeSort(head), mergeSort(nextToMiddle))
    }

    private fun merge(left: Node?, right: Node?): Node? {
        val comparator = userType.getComparator()
        val dummy = Node(0)
        var tail = dummy
        var l = left
        var r = right

        while (l != null && r != null) {
            if (comparator.compare(l.data, r.data) < 0) {
                tail.next = l
                l = l.next
            } else {
                tail.next = r
                r = r.next
            }
            tail = tail.next!!
        }
        tail.next = l ?: r
        return dummy.next
    }

    private fun getMiddle(head: Node?): Node? {
        var slow = head
        var fast = head
        while (fast?.next?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }
        return slow
    }

    fun toList(): List<Any> {
        val result = mutableListOf<Any>()
        var current = head
        while (current != null) {
            result.add(current.data)
            current = current.next
        }
        return result
    }

    fun saveToFile(filename: String) {
        File(filename).writeText(this.toList().joinToString("\n") { 
            userType.parseValue(it.toString()).toString() 
        })
    }

    fun loadFromFile(filename: String) {
        File(filename).readLines().forEach { 
            add(userType.parseValue(it)) 
        }
    }
}