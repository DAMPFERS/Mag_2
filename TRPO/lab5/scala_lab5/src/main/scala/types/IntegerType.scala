// package types

import java.io.InputStreamReader
import java.io.BufferedReader

class IntegerType extends UserType {
  override def typeName: String = "Integer"

  override def create: Any = 0

  override def clone(obj: Any): Any = obj match {
    case i: Int => i 
    case _ => throw new IllegalArgumentException("Invalid Integer object")
  }

  override def readValue(in: InputStreamReader): Any = {
    val br = new BufferedReader(in)
    parseValue(br.readLine())
  }

  override def parseValue(ss: String): Any = ss.toInt

  override def getTypeComparator: Comparator = new Comparator {
    override def compare(o1: Any, o2: Any): Int = 
      o1.asInstanceOf[Int] - o2.asInstanceOf[Int]
  }
}