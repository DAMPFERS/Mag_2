pause
kotlinc $(ls *.kt) `
-cp "libs\tornadofx-1.7.20.jar;libs\javafx-sdk-21\lib\*" `
-include-runtime `
-d app.jar
pause


kotlinc MainApp.kt UserType.kt UserFactory.kt SinglyLinkedList.kt IntegerType.kt GPSCoordinateType.kt -cp "libs\tornadofx-1.7.20.jar;libs\javafx-sdk-21\lib\*" -include-runtime -d app.jar


kotlinc MainApp.kt UserType.kt UserFactory.kt SinglyLinkedList.kt IntegerType.kt GPSCoordinateType.kt -cp "libs\tornadofx-1.7.20.jar;libs\javafx-sdk-21\lib\javafx.controls.jar;libs\javafx-sdk-21\lib\javafx.fxml.jar;libs\javafx-sdk-21\lib\javafx.base.jar" -include-runtime -d app.jar

kotlinc MainApp.kt UserType.kt UserFactory.kt SinglyLinkedList.kt IntegerType.kt GPSCoordinateType.kt -cp "libs/tornadofx-1.7.20.jar;libs/javafx-sdk-21/lib/javafx.controls.jar;libs/javafx-sdk-21/lib/javafx.fxml.jar;libs/javafx-sdk-21/lib/javafx.base.jar;libs/javafx-sdk-21/lib/javafx.graphics.jar" -include-runtime -d app.jar