package com.example.project_caxfix.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CiviFixDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_REPORTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AUTHOR_NAME TEXT NOT NULL,
                $COLUMN_NEIGHBORHOOD TEXT,
                $COLUMN_STATUS TEXT NOT NULL,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_IMAGE_URI TEXT,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_SUPPORT_COUNT INTEGER DEFAULT 0,
                $COLUMN_COMMENT_COUNT INTEGER DEFAULT 0
            );
        """.trimIndent()

        db.execSQL(createTableQuery)

        // Insertar datos iniciales de demostración en SQLite
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORTS")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val sampleSql = """
            INSERT INTO $TABLE_REPORTS 
            ($COLUMN_AUTHOR_NAME, $COLUMN_NEIGHBORHOOD, $COLUMN_STATUS, $COLUMN_TITLE, $COLUMN_CATEGORY, $COLUMN_DESCRIPTION, $COLUMN_CREATED_AT, $COLUMN_SUPPORT_COUNT, $COLUMN_COMMENT_COUNT)
            VALUES 
            ('Cristopher Cruzado', 'Barrio San Sebastián', 'En Atención', 'Hundimiento y bache profundo en Jr. Dos de Mayo', 'Baches y Pistas', 'El bache se generó tras las lluvias y está causando problemas en el tránsito de vehículos ligeros y transporte público.', ${now - 2700000}, 24, 8),
            ('Carolina Zapata', 'Plazuela Belén', 'Pendiente', 'Luminaria apagada en Plazuela Belén', 'Alumbrado Público', 'El poste de alumbrado público frente a la iglesia lleva 3 días sin funcionar, dejando la zona a oscuras durante la noche.', ${now - 7200000}, 18, 5),
            ('Miguel Ángel Cerna', 'Barrio Cumpampa', 'Resuelto', 'Acumulación de residuos sólidos en esquina con Av. Perú', 'Limpieza y Residuos', 'Vecinos reportan montículo de basura acumulada en la esquina. Se solicita presencia del camión recolector municipal.', ${now - 18000000}, 31, 12);
        """.trimIndent()
        db.execSQL(sampleSql)
    }

    companion object {
        private const val DATABASE_NAME = "civifix.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_REPORTS = "reports"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR_NAME = "author_name"
        const val COLUMN_NEIGHBORHOOD = "neighborhood"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_SUPPORT_COUNT = "support_count"
        const val COLUMN_COMMENT_COUNT = "comment_count"
    }
}