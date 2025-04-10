import java.io.InputStreamReader;
import java.io.IOException;

public interface UserType {
    String typeName();
    Object create();
    Object clone(Object obj);
    Object readValue(InputStreamReader in) throws IOException;
    Object parseValue(String ss);
    Comparator getTypeComparator();
}