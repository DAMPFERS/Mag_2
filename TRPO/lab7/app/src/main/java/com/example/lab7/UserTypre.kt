package com.example.lab7

import java.io.InputStream

interface UserType {
    fun typeName(): String
    fun create(): Any
    fun clone(obj: Any): Any
    fun readValue(input: InputStream): Any
    fun parseValue(ss: String): Any
    fun getComparator(): Comparator<Any>
}