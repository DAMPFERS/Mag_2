public class TypeFactory {
    public UserType getType(String typeName) {
        switch (typeName) {
            case "Integer":
                return new IntegerType();
            case "GPSCoordinate":
                return new GPSCoordinateType();
            default:
                throw new IllegalArgumentException("Unknown type: " + typeName);
        }
    }
}