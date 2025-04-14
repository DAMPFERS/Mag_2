package com.example.model 

import java.io.InputStream

interface TypeComparator {
    fun compare(o1: Any?, o2: Any?): Int
}

interface UserType {
    fun typeName(): String
    fun create(): Any
    fun clone(obj: Any): Any
    fun readValue(input: InputStream): Any
    fun parseValue(ss: String): Any
    fun getComparator(): TypeComparator
}