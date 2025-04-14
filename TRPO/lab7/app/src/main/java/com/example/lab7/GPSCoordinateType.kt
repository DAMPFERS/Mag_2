package com.example.lab7

import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class GPSCoordinate(val lat: Double, val lon: Double, val time: LocalDateTime) {
    override fun toString() = "%.6f;%.6f;%s".format(lat, lon, time.format(DateTimeFormatter.ISO_DATE_TIME))
}

class GPSCoordinateType : UserType {
    override fun typeName() = "GPSCoordinate"
    override fun create() = GPSCoordinate(0.0, 0.0, LocalDateTime.now())
    override fun clone(obj: Any) = (obj as GPSCoordinate).copy()
    override fun readValue(input: InputStream) = parseValue(input.bufferedReader().readLine())
    override fun parseValue(ss: String): Any {
        val parts = ss.split(";")
        return GPSCoordinate(
            parts[0].toDouble(),
            parts[1].toDouble(),
            LocalDateTime.parse(parts[2], DateTimeFormatter.ISO_DATE_TIME)
        )
    }
    override fun getComparator() = Comparator<Any> { o1, o2 ->
        (o1 as GPSCoordinate).time.compareTo((o2 as GPSCoordinate).time)
    }
}