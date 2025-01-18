package com.example.mytodoapp

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {

    companion object {
        const val ADD_TASK_REQUEST = 1 // Уникальный код для идентификации запроса
    }

    private lateinit var recyclerView: RecyclerView
    private val todoItemsRepository = TodoItemsRepository()
    private lateinit var tasksAdapter: TasksAdapter

    // Для отслеживания свайпа
    private val swipeLimit = 200f // 200dp в пикселях

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация RecyclerView и адаптера
        recyclerView = findViewById(R.id.recyclerViewTasks)
        setupRecyclerView()

        // Установка обработчика кнопки добавления задачи
        val roundButton: Button = findViewById(R.id.roundButton)
        roundButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK_REQUEST && resultCode == Activity.RESULT_OK) {
            val taskId = data?.getStringExtra("taskId")
            val taskText = data?.getStringExtra("taskText") ?: return
            val selectedPriority = data?.getStringExtra("selectedPriority") ?: "Обычная"
            val selectedDate = data?.getStringExtra("selectedDate")

            val parsedDate = if (!selectedDate.isNullOrEmpty()) {
                try {
                    SimpleDateFormat("dd MMMM yyyy", Locale("ru", "RU")).parse(selectedDate)
                } catch (e: ParseException) {
                    null
                }
            } else {
                null
            }

            if (taskId != null) {
                // Если taskId передан, обновляем существующую задачу
                val task = todoItemsRepository.getTodoItemById(taskId)
                task?.let {
                    it.text = taskText
                    it.priority = selectedPriority
                    it.deadline = parsedDate
                    todoItemsRepository.updateTodoItem(it)
                }
            } else {
                // Добавляем новую задачу
                val newTask = TodoItem(
                    id = UUID.randomUUID().toString(),
                    text = taskText,
                    priority = selectedPriority,
                    deadline = parsedDate,
                    isCompleted = false,
                    createdAt = Date()
                )
                todoItemsRepository.addTodoItem(newTask)
            }

            updateTodoList()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewTasks)

        tasksAdapter = TasksAdapter(todoItemsRepository.getTodoItems()) { task, isChecked ->
            task.isCompleted = isChecked
            todoItemsRepository.updateTodoItem(task)
            updateTodoList()
        }

        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Добавляем ItemTouchHelper для обработки свайпов
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            private val greenBackground = ColorDrawable(Color.parseColor("#34C759"))
            private val redBackground = ColorDrawable(Color.parseColor("#FF3B30"))
            private val checkMarkIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_check)
            private val deleteIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = todoItemsRepository.getTodoItems()[position]

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        // Завершить задачу
                        task.isCompleted = true
                        todoItemsRepository.updateTodoItem(task)
                    }
                    ItemTouchHelper.LEFT -> {
                        // Удалить задачу
                        todoItemsRepository.deleteTodoItem(task)
                    }
                }
                updateTodoList()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                // Ограничение значения dX
                val limitedDx = when {
                    dX > swipeLimit -> swipeLimit
                    dX < -swipeLimit -> -swipeLimit
                    else -> dX
                }

                if (limitedDx > 0) {
                    // Свайп вправо (зеленый фон с галочкой)
                    greenBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + limitedDx.toInt(),
                        itemView.bottom
                    )
                    greenBackground.draw(c)

                    checkMarkIcon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.left + 70
                        val iconRight = iconLeft + it.intrinsicWidth

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                } else if (limitedDx < 0) {
                    // Свайп влево (красный фон с мусоркой)
                    redBackground.setBounds(
                        itemView.right + limitedDx.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    redBackground.draw(c)

                    deleteIcon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.right - 70 - it.intrinsicWidth
                        val iconRight = itemView.right - 70

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }

                // Передаем ограниченное значение limitedDx
                super.onChildDraw(c, recyclerView, viewHolder, limitedDx, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateTodoList() {
        recyclerView.post {
            tasksAdapter.updateTasks(todoItemsRepository.getTodoItems())
        }
    }
}





