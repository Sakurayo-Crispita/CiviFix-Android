package com.example.project_caxfix.data;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class CiviFixDatabaseHelper extends SQLiteOpenHelper {
    private static volatile CiviFixDatabaseHelper instance;
    public static CiviFixDatabaseHelper get(Context context) {
        if (instance == null) synchronized (CiviFixDatabaseHelper.class) {
            if (instance == null) instance = new CiviFixDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    private CiviFixDatabaseHelper(Context context) { super(context, "civifix.db", null, 3); }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        createUserTables(db);
        db.execSQL("CREATE TABLE reports (id INTEGER PRIMARY KEY AUTOINCREMENT, author_name TEXT NOT NULL, " +
            "neighborhood TEXT, status TEXT NOT NULL, title TEXT NOT NULL, category TEXT NOT NULL, " +
            "description TEXT NOT NULL, image_uri TEXT, created_at INTEGER NOT NULL, " +
            "support_count INTEGER DEFAULT 0, comment_count INTEGER DEFAULT 0, " +
            "author_user_id INTEGER REFERENCES users(id), author_anonymous INTEGER NOT NULL DEFAULT 0)");
        createInteractionTables(db);
        sample(db, "Carolina Zapata", "Plazuela Belén", "Pendiente", "Luminaria apagada en Plazuela Belén", "Alumbrado Público",
            "El poste frente a la iglesia lleva varios días sin funcionar.", 7200000);
        sample(db, "Miguel Ángel Cerna", "Barrio Cumpampa", "Resuelto", "Residuos acumulados en Av. Perú", "Limpieza y Residuos",
            "Se atendió el punto de acumulación de residuos reportado por los vecinos.", 18000000);
        sample(db, "Christopher Cruzado", "Barrio San Sebastián", "En Atención", "Bache profundo en Jr. Dos de Mayo", "Baches y Pistas",
            "El bache generado tras las lluvias dificulta el tránsito. Se solicita su reparación.", 2700000);
    }
    private void createUserTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, email TEXT NOT NULL UNIQUE COLLATE NOCASE, dni TEXT NOT NULL UNIQUE, " +
            "neighborhood TEXT NOT NULL, password_salt TEXT NOT NULL, password_hash TEXT NOT NULL)");
    }
    private void createInteractionTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS supports (report_id INTEGER NOT NULL REFERENCES reports(id) ON DELETE CASCADE, " +
            "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, PRIMARY KEY(report_id, user_id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS comments (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "report_id INTEGER NOT NULL REFERENCES reports(id) ON DELETE CASCADE, " +
            "user_id INTEGER NOT NULL REFERENCES users(id), body TEXT NOT NULL, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reports_created ON reports(created_at)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_comments_report ON comments(report_id)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Conserva los reportes de la versión Kotlin anterior.
        if (oldVersion < 2) {
            createUserTables(db);
            db.execSQL("ALTER TABLE reports ADD COLUMN author_user_id INTEGER REFERENCES users(id)");
            createInteractionTables(db);
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE reports ADD COLUMN author_anonymous INTEGER NOT NULL DEFAULT 0");
            // Solo añade fotos a los tres reportes de demostración; conserva los reportes del alumno.
            demoPhoto(db, "Bache profundo en Jr. Dos de Mayo", "demo_pothole");
            demoPhoto(db, "Luminaria apagada en Plazuela Belén", "demo_lighting");
            demoPhoto(db, "Residuos acumulados en Av. Perú", "demo_resolved");
        }
    }
    private void demoPhoto(SQLiteDatabase db, String title, String drawable) {
        db.execSQL("UPDATE reports SET image_uri = ? WHERE title = ? AND author_user_id IS NULL AND (image_uri IS NULL OR image_uri = '')",
            new Object[]{"android.resource://com.example.project_caxfix/drawable/" + drawable, title});
    }
    private void sample(SQLiteDatabase db, String author, String neighborhood, String status,
                        String title, String category, String description, long age) {
        ContentValues v = new ContentValues();
        v.put("author_name", author); v.put("neighborhood", neighborhood); v.put("status", status);
        v.put("title", title); v.put("category", category); v.put("description", description);
        v.put("created_at", System.currentTimeMillis() - age);
        String drawable = category.equals("Baches y Pistas") ? "demo_pothole" : category.equals("Alumbrado Público") ? "demo_lighting" : "demo_resolved";
        v.put("image_uri", "android.resource://com.example.project_caxfix/drawable/" + drawable);
        db.insertOrThrow("reports", null, v);
    }
}
