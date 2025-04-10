import java.util.ArrayList;

public class UserFactory {
    private final ArrayList<UserType> typeList;

    public UserFactory() {
        typeList = new ArrayList<>();
        typeList.add(new IntegerType());
        typeList.add(new GPSCoordinateType());
    }

    public ArrayList<String> getTypeNameList() {
        ArrayList<String> names = new ArrayList<>();
        for (UserType type : typeList) {
            names.add(type.typeName());
        }
        return names;
    }

    public UserType getBuilderByName(String name) {
        for (UserType type : typeList) {
            if (type.typeName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type not found: " + name);
    }
}