package com.example.project_caxfix

data class Report(
    val id: String = "0",
    val authorName: String = "Cristopher Cruzado",
    val neighborhood: String = "Cajamarca",
    val timeAgo: String = "",
    val status: String = "Pendiente",
    val title: String,
    val category: String = "General",
    val description: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val supportCount: Int = 0,
    val commentCount: Int = 0
) {
    fun getFormattedTimeAgo(): String {
        if (timeAgo.isNotBlank()) return timeAgo
        val diffMs = System.currentTimeMillis() - createdAt
        if (diffMs <= 0) return "Hace un momento"
        val minutes = diffMs / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "Hace un momento"
            minutes < 60 -> "Hace $minutes min"
            hours < 24 -> "Hace $hours h"
            else -> "Hace $days d"
        }
    }
}