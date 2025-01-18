package com.example.mytodoapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.*


class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtask)

        // Инициализация Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Устанавливаем пустой заголовок
        supportActionBar?.title = ""

        // Инициализация кнопки "крестик"
        val closeButton: View = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            finish() // Закрываем текущую активность и возвращаемся к предыдущей
        }

        // Получаем ссылку на Spinner
        val spinner: Spinner = findViewById(R.id.spinnerPriority)

        // Получаем массив строковых значений из ресурсов
        val priorityOptions = resources.getStringArray(R.array.priority_array)

        // Создаем адаптер для Spinner, используя стандартный layout для элементов
        val adapter = CustomSpinnerAdapter(this, priorityOptions)

        // Устанавливаем стиль для выпадающего списка
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Применяем адаптер к Spinner
        spinner.adapter = adapter

        // Устанавливаем слушатель для выбора элемента в Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Получаем выбранную строку
                val selectedPriority = priorityOptions[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Ничего не выбрано, если пользователь не выбрал элемент
            }
        }

        // Устанавливаем начальное значение (например, "Нет")
        spinner.setSelection(0)  // Индекс первого элемента

        // Получаем ссылку на Switch и TextView из разметки
        val mySwitch: Switch = findViewById(R.id.mySwitch)
        val selectedDateText: TextView = findViewById(R.id.selectedDateText)

        // Устанавливаем обработчик для переключения Switch
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Если Switch включен, показываем DatePickerDialog
                showDatePickerDialog(selectedDateText)
            }
        }

        // Получаем ссылку на кнопку по ID
        val buttonSave: Button = findViewById(R.id.buttonSave)

        // Устанавливаем обработчик нажатия на кнопку
        buttonSave.setOnClickListener {
            val taskText = findViewById<EditText>(R.id.editTextTask).text.toString()
            val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
            val selectedPriority = spinnerPriority.selectedItem.toString()
            val selectedDate = findViewById<TextView>(R.id.selectedDateText).text.toString()

            if (taskText.isBlank()) {
                Toast.makeText(this, "Введите текст задачи", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("taskText", taskText)
                putExtra("selectedPriority", selectedPriority)
                putExtra("selectedDate", selectedDate)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Завершаем активность
        }

        val buttonDelete: Button = findViewById(R.id.buttonDelete)

        buttonDelete.setOnClickListener {
            // Очищаем текст задачи
            val editTextTask = findViewById<EditText>(R.id.editTextTask)
            editTextTask.text.clear()

            // Сбрасываем значение Spinner
            val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
            spinnerPriority.setSelection(0)

            // Очищаем текст выбранной даты
            val selectedDateText = findViewById<TextView>(R.id.selectedDateText)
            selectedDateText.text = ""
        }

        val taskId = intent.getStringExtra("taskId")
        val taskText = intent.getStringExtra("taskText")
        val taskPriority = intent.getStringExtra("taskPriority")
        val taskDeadline = intent.getLongExtra("taskDeadline", -1L)

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)

        editTextTask.setText(taskText)
        spinnerPriority.setSelection(
            resources.getStringArray(R.array.priority_array).indexOf(taskPriority)
        )

        if (taskDeadline != -1L) {
            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale("ru", "RU")).format(Date(taskDeadline))
            selectedDateText.text = formattedDate
        }

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("taskId", taskId)
                putExtra("taskText", editTextTask.text.toString())
                putExtra("selectedPriority", spinnerPriority.selectedItem.toString())
                putExtra("selectedDate", selectedDateText.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun showDatePickerDialog(selectedDateText: TextView) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val locale = Locale("ru", "RU")
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", locale)

        val datePickerDialog = DatePickerDialog(
            this, // Используем контекст Activity
            R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateCalendar = Calendar.getInstance(Locale("ru", "RU")).apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }

                val formattedDate = dateFormat.format(selectedDateCalendar.time)

                selectedDateText.text = formattedDate
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    class CustomSpinnerAdapter(context: Context, items: Array<String>) :
        ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {

        // Метод для отображения выбранного элемента в Spinner (не изменяем цвет здесь)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Получаем стандартное представление для элемента
            val view = super.getView(position, convertView, parent)

            // Находим TextView, в котором отображается выбранный элемент
            val textView = view.findViewById<TextView>(android.R.id.text1)

            // Меняем цвет текста для выбранного элемента
            textView.setTextColor(Color.parseColor("#4D000000"))

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

            return view
        }


        // Метод для отображения элементов в выпадающем списке
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Получаем представление для элемента выпадающего списка
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)

            // Проверяем, если элемент "!! Высокий", устанавливаем красный цвет
            if (getItem(position) == "!! Высокий") {
                textView.setTextColor(Color.parseColor("#FF3B30"))  // Красный цвет для "!! Высокий"
            } else {
                textView.setTextColor(Color.BLACK)  // Черный цвет для остальных элементов
            }

            return view
        }


    }
}