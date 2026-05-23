package com.example.pan.model

data class CourseEntry(
    val id:   String,
    val name: String,
    val ECTS: Double,
    val type: String
)

fun loadCoursesFromAssets(context: android.content.Context): List<CourseEntry> {
    val json  = context.assets.open("courses.json").bufferedReader().use { it.readText() }
    val array = org.json.JSONArray(json)
    return List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        CourseEntry(
            id   = obj.getString("id"),
            name = obj.getString("name"),
            ECTS = obj.getDouble("ECTS"),
            type = obj.getString("type")
        )
    }
}