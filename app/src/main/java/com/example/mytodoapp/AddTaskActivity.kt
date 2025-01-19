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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.*


class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtask)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // setting an empty header
        supportActionBar?.title = ""

        // initializing the cross button
        val closeButton: View = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            finish() // close the current activity and return to the previous one
        }

        // get a link to the Spinner
        val spinner: Spinner = findViewById(R.id.spinnerPriority)

        // get an array of string values from resources
        val priorityOptions = resources.getStringArray(R.array.priority_array)

        val adapter = CustomSpinnerAdapter(this, priorityOptions)

        // setting the style for the drop-down list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // applying the adapter to the Spinner
        spinner.adapter = adapter

        // setting a listener to select an item in the Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // nothing is selected if the user has not selected the item.
            }
        }

        // setting the initial value
        spinner.setSelection(0)

        // get a link to the Switch and TextView from the markup
        val mySwitch: SwitchMaterial = findViewById(R.id.mySwitch)
        val selectedDateText: TextView = findViewById(R.id.selectedDateText)

        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePickerDialog(selectedDateText)
            }
        }

        val buttonSave: Button = findViewById(R.id.buttonSave)

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
            finish()
        }

        val buttonDelete: Button = findViewById(R.id.buttonDelete)

        buttonDelete.setOnClickListener {
            val editTextTask = findViewById<EditText>(R.id.editTextTask)
            editTextTask.text.clear()

            val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
            spinnerPriority.setSelection(0)

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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)

            val textView = view.findViewById<TextView>(android.R.id.text1)

            textView.setTextColor(Color.parseColor("#4D000000"))

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

            return view
        }


        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)

            if (getItem(position) == "!! Высокий") {
                textView.setTextColor(Color.parseColor("#FF3B30"))  // Красный цвет для "!! Высокий"
            } else {
                textView.setTextColor(Color.BLACK)  // Черный цвет для остальных элементов
            }

            return view
        }


    }
}