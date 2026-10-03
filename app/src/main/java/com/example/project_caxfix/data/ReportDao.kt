package com.example.project_caxfix.data

import android.content.ContentValues
import android.content.Context
import com.example.project_caxfix.Report

class ReportDao(context: Context) {

    private val dbHelper = CiviFixDatabaseHelper(context)

    fun insertReport(report: Report): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CiviFixDatabaseHelper.COLUMN_AUTHOR_NAME, report.authorName)
            put(CiviFixDatabaseHelper.COLUMN_NEIGHBORHOOD, report.neighborhood)
            put(CiviFixDatabaseHelper.COLUMN_STATUS, report.status)
            put(CiviFixDatabaseHelper.COLUMN_TITLE, report.title)
            put(CiviFixDatabaseHelper.COLUMN_CATEGORY, report.category)
            put(CiviFixDatabaseHelper.COLUMN_DESCRIPTION, report.description)
            put(CiviFixDatabaseHelper.COLUMN_IMAGE_URI, report.imageUri)
            put(CiviFixDatabaseHelper.COLUMN_CREATED_AT, report.createdAt)
            put(CiviFixDatabaseHelper.COLUMN_SUPPORT_COUNT, report.supportCount)
            put(CiviFixDatabaseHelper.COLUMN_COMMENT_COUNT, report.commentCount)
        }
        return db.insert(CiviFixDatabaseHelper.TABLE_REPORTS, null, values)
    }

    fun getAllReports(): MutableList<Report> {
        val reportsList = mutableListOf<Report>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            CiviFixDatabaseHelper.TABLE_REPORTS,
            null,
            null,
            null,
            null,
            null,
            "${CiviFixDatabaseHelper.COLUMN_CREATED_AT} DESC"
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                val idIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_ID)
                val authorIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_AUTHOR_NAME)
                val neighborhoodIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_NEIGHBORHOOD)
                val statusIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_STATUS)
                val titleIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_TITLE)
                val categoryIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_CATEGORY)
                val descriptionIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_DESCRIPTION)
                val imageUriIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_IMAGE_URI)
                val createdAtIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_CREATED_AT)
                val supportCountIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_SUPPORT_COUNT)
                val commentCountIndex = c.getColumnIndexOrThrow(CiviFixDatabaseHelper.COLUMN_COMMENT_COUNT)

                do {
                    val report = Report(
                        id = c.getLong(idIndex).toString(),
                        authorName = c.getString(authorIndex) ?: "Vecino de Cajamarca",
                        neighborhood = c.getString(neighborhoodIndex) ?: "Cajamarca",
                        status = c.getString(statusIndex) ?: "Pendiente",
                        title = c.getString(titleIndex) ?: "",
                        category = c.getString(categoryIndex) ?: "General",
                        description = c.getString(descriptionIndex) ?: "",
                        imageUri = c.getString(imageUriIndex),
                        createdAt = c.getLong(createdAtIndex),
                        supportCount = c.getInt(supportCountIndex),
                        commentCount = c.getInt(commentCountIndex)
                    )
                    reportsList.add(report)
                } while (c.moveToNext())
            }
        }
        return reportsList
    }
}