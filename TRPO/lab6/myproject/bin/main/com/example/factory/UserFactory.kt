package com.example.factory

import com.example.model.UserType 
import com.example.model.IntegerType
import com.example.model.GPSCoordinateType

class UserFactory {
    private val types = listOf(IntegerType(), GPSCoordinateType())

    fun getTypeNames() = types.map { it.typeName() }

    fun getBuilderByName(name: String): UserType {
        return types.find { it.typeName() == name } 
            ?: throw IllegalArgumentException("Тип $name не поддерживается")
    }
}