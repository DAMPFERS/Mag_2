//package types

import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GPSCoordinate(var latitude: Double, var longitude: Double, var timestamp: LocalDateTime) {
  override def toString: String = 
    f"$latitude%.6f;$longitude%.6f;${timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
}

class GPSCoordinateType extends UserType {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  override def typeName: String = "GPSCoordinate"

  override def create: Any = new GPSCoordinate(0.0, 0.0, LocalDateTime.now())

  override def clone(obj: Any): Any = obj match {
    case gps: GPSCoordinate => new GPSCoordinate(gps.latitude, gps.longitude, gps.timestamp)
    case _ => throw new IllegalArgumentException
  }

  override def readValue(in: InputStreamReader): Any = {
    val br = new java.io.BufferedReader(in)
    parseValue(br.readLine())
  }

  override def parseValue(ss: String): Any = {
    val parts = ss.split(";")
    new GPSCoordinate(
      parts(0).toDouble,
      parts(1).toDouble,
      LocalDateTime.parse(parts(2), formatter)
    )
  }

  override def getTypeComparator: Comparator = new Comparator {
    override def compare(o1: Any, o2: Any): Int = 
      o1.asInstanceOf[GPSCoordinate].latitude.compareTo(o2.asInstanceOf[GPSCoordinate].latitude)
  }
}