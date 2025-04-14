import java.io.InputStream

class IntegerType : UserType {
    override fun typeName() = "Integer"

    override fun create() = 0

    override fun clone(obj: Any) = (obj as Int)

    override fun readValue(input: InputStream) = 
        parseValue(input.bufferedReader().readLine())

    override fun parseValue(ss: String) = ss.toInt()

    override fun getComparator() = object : Comparator {
        override fun compare(o1: Any?, o2: Any?) = 
            (o1 as Int).compareTo(o2 as Int)
    }
}