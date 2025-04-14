class UserFactory {
    private val types = listOf(IntegerType(), GPSCoordinateType())

    fun getTypeNames() = types.map { it.typeName() }

    fun getBuilderByName(name: String): UserType {
        return types.find { it.typeName() == name } 
            ?: throw IllegalArgumentException("Тип $name не поддерживается")
    }
}