package com.example.mytodoapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(
    private var tasks: List<TodoItem>,
    private val onTaskChecked: (TodoItem, Boolean) -> Unit,
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    // ViewHolder for working with list items
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTask: TextView = itemView.findViewById(R.id.textViewTask)
        private val checkBoxCompleteTask: CheckBox = itemView.findViewById(R.id.checkBoxCompleteTask)
        private val textViewDeadline: TextView = itemView.findViewById(R.id.textViewDeadline)
        private val infoButton: ImageButton = itemView.findViewById(R.id.infoButton)
        private val textViewPriority: TextView = itemView.findViewById(R.id.textViewPriority)

        fun bind(task: TodoItem) {
            textViewTask.text = task.text
            textViewDeadline.text = task.deadline?.let {
                SimpleDateFormat("dd MMM yyyy", Locale("ru", "RU")).format(it)
            } ?: ""

            if (task.priority == "!! Высокий") {
                textViewPriority.visibility = View.VISIBLE
            } else {
                textViewPriority.visibility = View.GONE
            }

            checkBoxCompleteTask.isChecked = task.isCompleted

            if (task.isCompleted) {
                textViewTask.paintFlags = textViewTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textViewTask.setTextColor(Color.GRAY)
            } else {
                textViewTask.paintFlags = textViewTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                textViewTask.setTextColor(Color.BLACK)
            }

            checkBoxCompleteTask.setOnCheckedChangeListener { _, isChecked ->
                onTaskChecked(task, isChecked)
            }

            // handler for clicking on the infoButton
            infoButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AddTaskActivity::class.java).apply {
                    putExtra("taskId", task.id)
                    putExtra("taskText", task.text)
                    putExtra("taskPriority", task.priority)
                    putExtra("taskDeadline", task.deadline?.time ?: -1L)
                }
                (context as Activity).startActivityForResult(intent, MainActivity.ADD_TASK_REQUEST)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // creating a list item view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // binding data to a ViewHolder
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        // returning the number of tasks
        return tasks.size
    }

    fun updateTasks(newTasks: List<TodoItem>) {
        // updating the task list
        tasks = newTasks
        notifyDataSetChanged()
    }
}





