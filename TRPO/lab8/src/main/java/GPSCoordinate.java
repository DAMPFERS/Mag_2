import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class GPSCoordinate implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;

    public GPSCoordinate() {
        this(0.0, 0.0, LocalDateTime.now());
    }

    public GPSCoordinate(double latitude, double longitude, LocalDateTime timestamp) {
        setLatitude(latitude);
        setLongitude(longitude);
        setTimestamp(timestamp);
    }

    public GPSCoordinate(GPSCoordinate other) {
        this(other.latitude, other.longitude, other.timestamp);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        this.timestamp = timestamp;
    }

    public double distanceTo(GPSCoordinate other) {
        final int R = 6371;

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    @Override
    public String toString() {
        return String.format("%.6f;%.6f;%s", 
            latitude, 
            longitude, 
            timestamp.format(TIME_FORMATTER));
    }

    public static GPSCoordinate fromString(String s) {
        String[] parts = s.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid string format");
        }
        try {
            // Заменяем NumberFormat на прямое преобразование
            double lat = Double.parseDouble(parts[0]); // Используем Double.parseDouble
            double lon = Double.parseDouble(parts[1]);
            LocalDateTime time = LocalDateTime.parse(parts[2], TIME_FORMATTER);
            return new GPSCoordinate(lat, lon, time);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing data", e);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeObject(timestamp);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        double lat = in.readDouble();
        double lon = in.readDouble();
        LocalDateTime time = (LocalDateTime) in.readObject();
        
        setLatitude(lat);
        setLongitude(lon);
        setTimestamp(time);
    }
}