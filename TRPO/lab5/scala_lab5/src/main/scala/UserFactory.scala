class UserFactory {
  private val types = List(new IntegerType, new GPSCoordinateType)

  def getTypeNameList: List[String] = types.map(_.typeName)
  
  def getBuilderByName(name: String): UserType = 
    types.find(_.typeName == name).getOrElse(throw new IllegalArgumentException)
}