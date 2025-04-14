import java.io.{InputStreamReader, Serializable}

trait Comparator extends Serializable {
  def compare(o1: Any, o2: Any): Int
}

trait UserType extends Serializable {
  def typeName: String
  def create: Any
  def clone(obj: Any): Any
  def readValue(in: InputStreamReader): Any
  def parseValue(ss: String): Any
  def getTypeComparator: Comparator
}