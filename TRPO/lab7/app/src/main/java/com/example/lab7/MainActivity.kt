package com.example.lab7

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var list: SinglyLinkedList
    private val factory = UserFactory()
    private val filename = "data.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Настройка Spinner
        val types = factory.getTypeNames()
        findViewById<Spinner>(R.id.typeSpinner).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, types)
        }

        // Инициализация списка
        list = SinglyLinkedList(factory.getBuilderByName(types[0]))

        // Обработчики кнопок
        findViewById<Button>(R.id.addButton).setOnClickListener { addItem() }
        findViewById<Button>(R.id.removeButton).setOnClickListener { removeItem() }
        findViewById<Button>(R.id.sortButton).setOnClickListener { sortList() }
        findViewById<Button>(R.id.saveButton).setOnClickListener { saveToFile() }
        findViewById<Button>(R.id.loadButton).setOnClickListener { loadFromFile() }

        updateListView()
    }

    private fun addItem() {
        val input = findViewById<EditText>(R.id.inputField).text.toString()
        try {
            val parsed = list.userType.parseValue(input)
            list.add(parsed)
            updateListView()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItem() {
        val index = findViewById<EditText>(R.id.indexField).text.toString().toIntOrNull()
        if (index == null || !list.remove(index)) {
            Toast.makeText(this, "Неверный индекс!", Toast.LENGTH_SHORT).show()
        } else {
            updateListView()
        }
    }

    private fun saveToFile() {
        File(filesDir, filename).writeText(list.toList().joinToString("\n") { it.toString() })
        Toast.makeText(this, "Данные сохранены!", Toast.LENGTH_SHORT).show()
    }

    private fun loadFromFile() {
        val file = File(filesDir, filename)
        if (!file.exists()) {
            Toast.makeText(this, "Файл не найден!", Toast.LENGTH_SHORT).show()
            return
        }
        list = SinglyLinkedList(list.userType)
        file.readLines().forEach { list.add(list.userType.parseValue(it)) }
        updateListView()
        Toast.makeText(this, "Данные загружены!", Toast.LENGTH_SHORT).show()
    }

    private fun sortList() {
        list.sort() // Вызов метода сортировки
        updateListView()
        Toast.makeText(this, "Список отсортирован!", Toast.LENGTH_SHORT).show()
    }

    private fun updateListView() {
        findViewById<ListView>(R.id.listView).adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            list.toList().map { it.toString() }
        )
    }
}