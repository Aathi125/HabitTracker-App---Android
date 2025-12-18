package com.example.labtest03.models

data class Habit(
    val id: String,
    var title: String,
    var completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)