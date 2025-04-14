trait Comparator {
  def compare(o1: Any, o2: Any): Int
}

trait UserType {
  def typeName: String
  def create: Any
  def clone(obj: Any): Any
  def readValue(in: java.io.InputStreamReader): Any
  def parseValue(ss: String): Any
  def getTypeComparator: Comparator
}