import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ComboBox, ListView, TextField}
import scalafx.collections.ObservableBuffer

object MainApp extends JFXApp {
  private val factory = new UserFactory
  private var currentList = new SinglyLinkedList(factory.getBuilderByName("Integer"))

  stage = new JFXApp.PrimaryStage {
    title = "GPS Linked List GUI"
    scene = new Scene(600, 400) {
      val typeCombo = new ComboBox[String](factory.getTypeNameList)
      val inputField = new TextField { promptText = "Введите данные (lat;lon;time)" }
      val addButton = new Button("Добавить")
      val listView = new ListView[String]

      content = new javafx.scene.layout.VBox {
        children = Seq(
          typeCombo,
          new javafx.scene.layout.HBox {
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
          case e: Exception => showError("Ошибка парсинга: " + e.getMessage)
        }
      }

      def updateListView(): Unit = {
        listView.items = ObservableBuffer(currentList.toList.map(_.toString))
      }
    }
  }

  private def showError(message: String): Unit = {
    new Alert(Alert.AlertType.Error) {
      title = "Ошибка"
      headerText = message
    }.showAndWait()
  }
}