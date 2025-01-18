package com.example.mytodoapp

import java.util.*

data class TodoItem(
    val id: String,
    var text: String,
    var priority: String,
    var deadline: Date? = null,
    var isCompleted: Boolean,
    val createdAt: Date,
    val updatedAt: Date? = null
)
