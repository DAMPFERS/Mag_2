import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class GPSCoordinateType implements UserType {
    @Override
    public String typeName() {
        return "GPSCoordinate";
    }

    @Override
    public Object create() {
        return new GPSCoordinate();
    }

    @Override
    public Object clone(Object obj) {
        if (!(obj instanceof GPSCoordinate)) 
            throw new IllegalArgumentException("Invalid object type");
        GPSCoordinate original = (GPSCoordinate) obj;
        return new GPSCoordinate(original.getLatitude(), original.getLongitude(), original.getTimestamp());
    }

    @Override
    public Object readValue(InputStreamReader in) throws IOException {
        BufferedReader reader = new BufferedReader(in);
        return parseValue(reader.readLine());
    }

    @Override
    public Object parseValue(String ss) {
        return GPSCoordinate.fromString(ss);
    }

    @Override
    public Comparator getTypeComparator() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                GPSCoordinate g1 = (GPSCoordinate) o1;
                GPSCoordinate g2 = (GPSCoordinate) o2;
                return Double.compare(g1.getLatitude(), g2.getLatitude());
            }
        };
    }
}