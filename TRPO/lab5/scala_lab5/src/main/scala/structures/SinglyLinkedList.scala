//package structures

class SinglyLinkedList(userType: UserType) {
  private case class Node(var data: Any, var next: Node = null)
  private var head: Node = null
  private var size = 0

  // Добавление в конец
  def add(value: Any): Unit = {
    val newNode = Node(userType.clone(value))
    if (head == null) head = newNode
    else {
      var current = head
      while (current.next != null) current = current.next
      current.next = newNode
    }
    size += 1
  }

  // Сортировка слиянием (функциональный стиль)
  def functionalSort: SinglyLinkedList = {
    def mergeSort(list: List[Any]): List[Any] = list match {
      case Nil => list
      case _ :: Nil => list
      case _ =>
        val (left, right) = list.splitAt(list.length / 2)
        merge(mergeSort(left), mergeSort(right))
    }

    def merge(left: List[Any], right: List[Any]): List[Any] = (left, right) match {
      case (Nil, _) => right
      case (_, Nil) => left
      case (l :: ls, r :: rs) =>
        if (userType.getTypeComparator.compare(l, r) < 0) l :: merge(ls, right)
        else r :: merge(left, rs)
    }

    val sortedList = new SinglyLinkedList(userType)
    mergeSort(toList).foreach(sortedList.add)
    sortedList
  }

  // Преобразование в список
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