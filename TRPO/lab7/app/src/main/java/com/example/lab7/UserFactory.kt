package com.example.lab7

class UserFactory {
    private val types = listOf(IntegerType(), GPSCoordinateType())
    fun getTypeNames() = types.map { it.typeName() }
    fun getBuilderByName(name: String) = types.find { it.typeName() == name }
        ?: throw IllegalArgumentException("Тип $name не поддерживается")
}