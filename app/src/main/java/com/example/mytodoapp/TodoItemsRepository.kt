package com.example.mytodoapp

class TodoItemsRepository {

    private val todoItems: MutableList<TodoItem> = mutableListOf()

    // method of adding a new case
    fun addTodoItem(todoItem: TodoItem) {
        todoItems.add(todoItem)
    }

    // method for getting a list of all cases
    fun getTodoItems(): List<TodoItem> {
        return todoItems
    }

    // method for getting cases by ID
    fun getTodoItemById(id: String): TodoItem? {
        return todoItems.find { it.id == id }
    }

    // method for updating cases
    fun updateTodoItem(updatedItem: TodoItem) {
        val index = todoItems.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            todoItems[index] = updatedItem
        }
    }

    // method for deleting cases
    fun deleteTodoItem(task: TodoItem) {
        todoItems.removeIf { it.id == task.id }
    }
}