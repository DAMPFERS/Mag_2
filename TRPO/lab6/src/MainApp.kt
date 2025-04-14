// fun main() {
//     val factory = UserFactory()
//     val list = SinglyLinkedList(factory.getBuilderByName("GPSCoordinate"))

//     // Добавление GPS-координаты
//     list.add(list.userType.parseValue("29.934280;30.335098;2023-10-01T12:30:00"))
//     list.add(list.userType.parseValue("55.751244;37.618423;2023-10-01T12:00:00"))
//     list.add(list.userType.parseValue("59.934280;30.335098;2023-10-01T12:30:00"))

//     // Вывод списка
//     println("До сортировки:")
//     list.toList().forEach { println(it) }

//     // Сортировка
//     list.sort()
//     println("\nПосле сортировки:")
//     list.toList().forEach { println(it) }

//     // Удаление элемента
//     list.remove(0)
//     println("\nПосле удаления первого элемента:")
//     list.toList().forEach { println(it) }
// }






import tornadofx.*
import javafx.collections.FXCollections
import java.io.File
import javafx.application.Application
// import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class MainApp : App(MainView::class)

class MainView : View("Singly List Manager") {
    private val factory = UserFactory()
    private var list = SinglyLinkedList(factory.getBuilderByName("GPSCoordinate"))
    private val items = FXCollections.observableArrayList<String>()

    // Элементы управления
    private val typeCombo = combobox<String> {
        items.setAll(factory.getTypeNames())
        selectionModel.selectFirst()
        action { updateListType() }
    }
    private val inputField = textfield { promptText = "Введите данные (lat;lon;time)" }
    private val indexField = textfield { promptText = "Индекс" }
    private val listView = listview(items)

    override val root = vbox {
        hbox {
            add(typeCombo)
            add(inputField)
            add(indexField)
            vbox {
                button("Добавить") { action { addItem() } }
                button("Получить") { action { getItem() } }
                button("Удалить") { action { removeItem() } }
                button("Сортировать") { action { sortList() } }
                button("Сохранить") { action { saveToFile() } }
                button("Загрузить") { action { loadFromFile() } }
            }
        }
        add(listView)
    }

    // Обновление типа данных
    private fun updateListType() {
        list = SinglyLinkedList(factory.getBuilderByName(typeCombo.value))
        updateListView()
    }

    // Обновление списка
    private fun updateListView() {
        items.setAll(list.toList().mapIndexed { idx, item -> "$idx: $item" })
    }

    // Обработчики событий
    private fun addItem() {
        try {
            val value = list.userType.parseValue(inputField.text)
            list.add(value)
            updateListView()
        } catch (e: Exception) {
            error("Ошибка добавления", e.message ?: "Неверный формат данных")
        }
    }

    private fun getItem() {
        try {
            val index = indexField.text.toInt()
            val value = list.get(index)
            information("Значение", value?.toString() ?: "Не найдено")
        } catch (e: Exception) {
            error("Ошибка", "Неверный индекс")
        }
    }

    private fun removeItem() {
        try {
            val index = indexField.text.toInt()
            if (list.remove(index)) updateListView()
            else error("Ошибка", "Неверный индекс")
        } catch (e: Exception) {
            error("Ошибка", "Ошибка удаления")
        }
    }

    private fun sortList() {
        list.sort()
        updateListView()
    }

    private fun saveToFile() {
        list.saveToFile("data.bin")
        information("Успех", "Данные сохранены в data.bin")
    }

    private fun loadFromFile() {
        list = SinglyLinkedList(list.userType).apply { loadFromFile("data.bin") }
        updateListView()
        information("Успех", "Данные загружены из data.bin")
    }
}