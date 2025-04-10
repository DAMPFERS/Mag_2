import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class IntegerType implements UserType {
    @Override
    public String typeName() {
        return "Integer";
    }

    @Override
    public Object create() {
        return 0;
    }

    @Override
    public Object clone(Object obj) {
        if (!(obj instanceof Integer)) 
            throw new IllegalArgumentException("Invalid object type");
        return Integer.valueOf((Integer) obj);
    }

    @Override
    public Object readValue(InputStreamReader in) throws IOException {
        BufferedReader reader = new BufferedReader(in);
        return parseValue(reader.readLine());
    }

    @Override
    public Object parseValue(String ss) {
        return Integer.parseInt(ss);
    }

    @Override
    public Comparator getTypeComparator() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return Integer.compare((Integer) o1, (Integer) o2);
            }
        };
    }
}