import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{VBox, HBox}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import java.io.File

object MainApp extends JFXApp {
  private var currentList = new SinglyLinkedList(new IntegerType)
  private val factory = new UserFactory

  // Объявляем элементы управления как поля класса
  private var listView: ListView[String] = _
  private var inputField: TextField = _
  private var indexField: TextField = _

  stage = new JFXApp.PrimaryStage {
    title = "Singly List Manager"
    scene = new Scene(800, 600) {
      // Инициализация элементов
      val typeCombo = new ComboBox[String](factory.getTypeNameList)
      inputField = new TextField { promptText = "Значение" }
      indexField = new TextField { promptText = "Индекс" }
      val addButton = new Button("Добавить")
      val getButton = new Button("Получить")
      val removeButton = new Button("Удалить")
      val sortButton = new Button("Сортировать")
      val saveButton = new Button("Сохранить")
      val loadButton = new Button("Загрузить")
      listView = new ListView[String]()

      // Разметка
      root = new VBox {
        children = Seq(
          new HBox {
            children = Seq(
              typeCombo,
              inputField,
              indexField,
              new VBox {
                children = Seq(
                  addButton,
                  getButton,
                  removeButton,
                  sortButton,
                  saveButton,
                  loadButton
                )
              }
            )
          },
          listView
        )
      }

      // Обработчики событий
      addButton.onAction = _ => handleAdd()
      getButton.onAction = _ => handleGet()
      removeButton.onAction = _ => handleRemove()
      sortButton.onAction = _ => handleSort()
      saveButton.onAction = _ => handleSave()
      loadButton.onAction = _ => handleLoad()
      
      typeCombo.onAction = _ => {
        currentList = new SinglyLinkedList(factory.getBuilderByName(typeCombo.value()))
        updateListView()
      }
    }
  }

  private def updateListView(): Unit = {
    listView.items = ObservableBuffer.from(
      currentList.toList.zipWithIndex.map { case (item, idx) => 
        s"$idx: ${item.toString}"
      }
    )
  }

  private def handleAdd(): Unit = try {
    val value = currentList.userType.parseValue(inputField.text())
    currentList.add(value)
    updateListView()
  } catch {
    case e: Exception => showError("Ошибка добавления", e)
  }

  private def handleGet(): Unit = try {
    val index = indexField.text().toInt
    currentList.get(index) match {
      case Some(value) => showInfo(s"Значение по индексу $index: ${value.toString}")
      case None => showError("Неверный индекс", new Exception("Индекс вне диапазона"))
    }
  } catch {
    case e: Exception => showError("Ошибка получения", e)
  }

  private def handleRemove(): Unit = try {
    val index = indexField.text().toInt
    if (currentList.remove(index)) updateListView()
    else showError("Неверный индекс", new Exception("Индекс вне диапазона"))
  } catch {
    case e: Exception => showError("Ошибка удаления", e)
  }

  private def handleSort(): Unit = {
    currentList.sort()
    updateListView()
  }

  private def handleSave(): Unit = {
    val file = new File("list.dat")
    currentList.saveToFile(file.getAbsolutePath)
    showInfo("Список сохранен в list.dat")
  }

  private def handleLoad(): Unit = try {
    val file = new File("list.dat")
    currentList = currentList.loadFromFile(file.getAbsolutePath)
    updateListView()
    showInfo("Список загружен из list.dat")
  } catch {
    case e: Exception => showError("Ошибка загрузки", e)
  }

  private def showError(message: String, e: Exception): Unit = {
    new Alert(AlertType.Error) {
      title = "Ошибка"
      headerText = s"$message: ${e.getMessage}"
    }.showAndWait()
  }

  private def showInfo(message: String): Unit = {
    new Alert(AlertType.Information) {
      title = "Информация"
      headerText = message
    }.showAndWait()
  }
}