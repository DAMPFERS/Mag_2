import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

class SinglyLinkedList(val userType: UserType) extends Serializable {
  private case class Node(var data: Any, var next: Node = null)
  private var head: Node = null
  private var size = 0

  // Добавление в конец
  def add(value: Any): Unit = {
    val clonedValue = userType.clone(value)
    val newNode = Node(clonedValue)
    
    if (head == null) {
      head = newNode
    } else {
      var current = head
      while (current.next != null) current = current.next
      current.next = newNode
    }
    size += 1
  }

  // Получение по индексу
  def get(index: Int): Option[Any] = {
    if (index < 0 || index >= size) return None
    
    var current = head
    for (_ <- 0 until index) current = current.next
    Some(current.data)
  }

  // Удаление по индексу
  def remove(index: Int): Boolean = {
    if (index < 0 || index >= size) return false
    
    if (index == 0) {
      head = head.next
    } else {
      var prev = head
      for (_ <- 0 until index - 1) prev = prev.next
      prev.next = prev.next.next
    }
    size -= 1
    true
  }

  // Сортировка слиянием
  def sort(): Unit = {
    def mergeSort(head: Node): Node = {
      if (head == null || head.next == null) return head
      
      val middle = getMiddle(head)
      val nextOfMiddle = middle.next
      middle.next = null
      
      val left = mergeSort(head)
      val right = mergeSort(nextOfMiddle)
      
      merge(left, right)
    }

    def merge(left: Node, right: Node): Node = {
      val comparator = userType.getTypeComparator
      val dummy = Node(null)
      var current = dummy
      var l = left
      var r = right
      
      while (l != null && r != null) {
        if (comparator.compare(l.data, r.data) < 0) {
          current.next = l
          l = l.next
        } else {
          current.next = r
          r = r.next
        }
        current = current.next
      }
      
      current.next = if (l != null) l else r
      dummy.next
    }
    
    head = mergeSort(head)
  }

  private def getMiddle(head: Node): Node = {
    var slow = head
    var fast = head
    while (fast.next != null && fast.next.next != null) {
      slow = slow.next
      fast = fast.next.next
    }
    slow
  }

  // Сохранение в файл
  def saveToFile(filename: String): Unit = {
    val oos = new ObjectOutputStream(new FileOutputStream(filename))
    oos.writeObject(this)
    oos.close()
  }

  // Загрузка из файла
  def loadFromFile(filename: String): SinglyLinkedList = {
    val ois = new ObjectInputStream(new FileInputStream(filename))
    val list = ois.readObject.asInstanceOf[SinglyLinkedList]
    ois.close()
    list
  }

  // Преобразование в список для GUI
  def toList: List[Any] = {
    var result = List.empty[Any]
    var current = head
    while (current != null) {
      result = result :+ current.data
      current = current.next
    }
    result
  }
}