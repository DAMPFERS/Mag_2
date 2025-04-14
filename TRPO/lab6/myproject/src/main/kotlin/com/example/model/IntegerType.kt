package com.example.model

import java.io.InputStream
import com.example.model.UserType
import com.example.model.TypeComparator

class IntegerType : UserType {
    override fun typeName() = "Integer"

    override fun create() = 0

    override fun clone(obj: Any) = (obj as Int)

    override fun readValue(input: InputStream) = 
        parseValue(input.bufferedReader().readLine())

    override fun parseValue(ss: String) = ss.toInt()

    override fun getComparator() = object : TypeComparator {
        override fun compare(o1: Any?, o2: Any?) = 
            (o1 as Int).compareTo(o2 as Int)
    }
}