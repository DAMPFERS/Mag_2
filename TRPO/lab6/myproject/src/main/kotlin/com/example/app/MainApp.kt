package com.example.app

import tornadofx.*
import javafx.collections.FXCollections
import javafx.beans.value.ObservableValue
import java.io.File
import com.example.factory.UserFactory
import com.example.model.UserType
import com.example.utils.SinglyLinkedList

class MainApp : App(MainView::class)

class MainView : View("Singly List Manager") {
    private val factory = UserFactory()
    private var list = SinglyLinkedList(factory.getBuilderByName("GPSCoordinate"))
    private val items = FXCollections.observableArrayList<String>()

    // Элементы управления GUI
    private val typeCombo = combobox<String> {
        items.setAll(factory.getTypeNames())
        selectionModel.selectFirst()
        // Исправление: явное указание типов в слушателе
        selectionModel.selectedItemProperty().addListener { 
            _: ObservableValue<out String>, 
            _: String?, 
            _: String? -> 
            updateListType() 
        }
    }


    private val inputField = textfield { 
        promptText = "Введите данные (например, 55.751244;37.618423;2023-10-01T12:00:00)" 
    }

    private val indexField = textfield { promptText = "Индекс" }
    private val listView = listview(items)

    override val root = vbox {
        spacing = 10.0
        
        hbox {
            spacing = 10.0
            add(typeCombo)
            add(inputField)
            add(indexField)
            
            vbox {
                spacing = 5.0
                button("Добавить").action { addItem() }
                button("Получить").action { getItem() }
                button("Удалить").action { removeItem() }
                button("Сортировать").action { sortList() }
                button("Сохранить").action { saveToFile() }
                button("Загрузить").action { loadFromFile() }
            }
        }
        
        add(listView)
    }

    // Обновление типа данных списка
    private fun updateListType() {
        list = SinglyLinkedList(factory.getBuilderByName(typeCombo.value))
        updateListView()
    }

    // Обновление отображаемого списка
    private fun updateListView() {
        items.setAll(list.toList().mapIndexed { idx, item -> "$idx: $item" })
    }

    // Обработчики событий
    private fun addItem() {
        try {
            val value = list.userType.parseValue(inputField.text)
            list.add(value)
            inputField.clear()
            updateListView()
        } catch (e: Exception) {
            error("Ошибка", "Неверный формат: ${e.message}")
        }
    }

    private fun getItem() {
        try {
            val index = indexField.text.toInt()
            val value = list.get(index)
            information("Значение", value?.toString() ?: "Не найдено")
            indexField.clear()
        } catch (e: Exception) {
            error("Ошибка", "Неверный индекс: ${e.message}")
        }
    }

    private fun removeItem() {
        try {
            val index = indexField.text.toInt()
            if (list.remove(index)) {
                updateListView()
                indexField.clear()
            } else {
                error("Ошибка", "Индекс $index не существует")
            }
        } catch (e: Exception) {
            error("Ошибка", "Некорректный индекс: ${e.message}")
        }
    }

    private fun sortList() {
        list.sort()
        updateListView()
        information("Успех", "Список отсортирован")
    }

    private fun saveToFile() {
        try {
            list.saveToFile("data.bin")
            information("Успех", "Данные сохранены в data.bin")
        } catch (e: Exception) {
            error("Ошибка", "Ошибка сохранения: ${e.message}")
        }
    }

    private fun loadFromFile() {
        try {
            list = SinglyLinkedList(list.userType).apply { loadFromFile("data.bin") }
            updateListView()
            information("Успех", "Данные загружены из data.bin")
        } catch (e: Exception) {
            error("Ошибка", "Ошибка загрузки: ${e.message}")
        }
    }
}