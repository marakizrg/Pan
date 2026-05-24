package com.example.pan.data.model

data class Course(
    val id: String,
    val title: String,
    val semester: String,
    val ects: String,
    val type: String,
    val professor: String,
    val description: String,
    val prerequisites: String
)
