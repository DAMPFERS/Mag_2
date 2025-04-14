package com.example.lab7

import java.io.InputStream

class IntegerType : UserType {
    override fun typeName() = "Integer"
    override fun create() = 0
    override fun clone(obj: Any) = obj as Int
    override fun readValue(input: InputStream) = input.bufferedReader().readLine().toInt()
    override fun parseValue(ss: String) = ss.toInt()
    override fun getComparator() = Comparator<Any> { o1, o2 -> (o1 as Int).compareTo(o2 as Int) }
}