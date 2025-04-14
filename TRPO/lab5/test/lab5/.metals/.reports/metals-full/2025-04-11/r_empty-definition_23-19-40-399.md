error id: `<none>`.
file:///C:/PROGRAMS/MAG/Mag_2/TRPO/lab5/test/lab5/src/main/scala/MainApp.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 544
uri: file:///C:/PROGRAMS/MAG/Mag_2/TRPO/lab5/test/lab5/src/main/scala/MainApp.scala
text:
```scala
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, Button, ComboBox, ListView, TextField}
import scalafx.scene.control.Alert.AlertType
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.{VBox, HBox}

object MainApp extends JFXApp {
  private val factory = new UserFactory
  private var currentList = new SinglyLinkedList(factory.getBuilderByName("Integer"))

  stage = new JFXApp.PrimaryStage {
    title = "GPS Linked List GUI"
    scene = new Scene(600, 400) {@@
      val typeCombo = new ComboBox[String](factory.getTypeNameList)
      val inputField = new TextField { promptText = "Введите данные (lat;lon;time)" }
      val addButton = new Button("Добавить")
      val listView = new ListView[String]()

      root = new VBox {
        children = Seq(
          typeCombo,
          new HBox {
            children = Seq(inputField, addButton)
          },
          listView
        )
      }

      addButton.onAction = _ => {
        try {
          val value = currentList.userType.parseValue(inputField.text())
          currentList.add(value)
          updateListView()
        } catch {
          case e: Exception => showError(s"Ошибка: ${e.getMessage}")
        }
      }

      def updateListView(): Unit = {
        listView.items = ObservableBuffer[String](currentList.toList.map(_.toString))
      }
    }
  }

  private def showError(message: String): Unit = {
    new Alert(AlertType.Error) {
      title = "Ошибка"
      headerText = message
    }.showAndWait()
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.