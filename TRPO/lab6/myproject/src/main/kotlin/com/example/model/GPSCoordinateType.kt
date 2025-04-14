package com.example.model


import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.model.UserType  // Добавить импорт интерфейса
import com.example.model.TypeComparator  // Переименованный интерфейс

data class GPSCoordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime
) {
    override fun toString(): String = 
        "%.6f;%.6f;%s".format(latitude, longitude, timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
}

class GPSCoordinateType : UserType {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun typeName() = "GPSCoordinate"

    override fun create() = GPSCoordinate(0.0, 0.0, LocalDateTime.now())

    override fun clone(obj: Any) = (obj as GPSCoordinate).copy()

    override fun readValue(input: InputStream): Any {
        return parseValue(input.bufferedReader().readLine())
    }

    override fun parseValue(ss: String): Any {
        val parts = ss.split(";")
        require(parts.size == 3) { "Неверный формат. Используйте: широта;долгота;время" }
        return GPSCoordinate(
            parts[0].toDouble().also { 
                require(it in -90.0..90.0) { "Широта должна быть в диапазоне [-90, 90]" } 
            },
            parts[1].toDouble().also { 
                require(it in -180.0..180.0) { "Долгота должна быть в диапазоне [-180, 180]" } 
            },
            LocalDateTime.parse(parts[2], formatter)
        )
    }

    override fun getComparator() = object : TypeComparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            val g1 = o1 as GPSCoordinate
            val g2 = o2 as GPSCoordinate
            return g1.timestamp.compareTo(g2.timestamp)
        }
    }
}