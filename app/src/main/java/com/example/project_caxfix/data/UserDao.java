package com.example.project_caxfix.data;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteConstraintException;
import com.example.project_caxfix.User;
import com.example.project_caxfix.security.PasswordHasher;
import java.util.Locale;

public final class UserDao {
    private final CiviFixDatabaseHelper helper;
    public UserDao(Context context) { helper = CiviFixDatabaseHelper.get(context); }
    public long register(String name, String email, String dni, String neighborhood, String password) {
        String salt = PasswordHasher.newSalt();
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("email", email.toLowerCase(Locale.ROOT)); v.put("dni", dni);
        v.put("neighborhood", neighborhood); v.put("password_salt", salt);
        v.put("password_hash", PasswordHasher.hash(password, salt));
        try { return helper.getWritableDatabase().insertOrThrow("users", null, v); }
        catch (SQLiteConstraintException e) { return -1; }
    }
    public User login(String identifier, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.query("users", null, "email = ? COLLATE NOCASE OR dni = ?", new String[]{identifier, identifier}, null, null, null)) {
            if (!c.moveToFirst()) return null;
            if (!PasswordHasher.verify(password, c.getString(c.getColumnIndexOrThrow("password_salt")), c.getString(c.getColumnIndexOrThrow("password_hash")))) return null;
            return read(c);
        }
    }
    public User get(long id) {
        try (Cursor c = helper.getReadableDatabase().query("users", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? read(c) : null;
        }
    }
    public boolean update(long id, String name, String neighborhood) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            ContentValues user = new ContentValues(); user.put("name", name); user.put("neighborhood", neighborhood);
            if (db.update("users", user, "id = ?", new String[]{String.valueOf(id)}) != 1) return false;
            ContentValues reports = new ContentValues(); reports.put("author_name", name); reports.put("neighborhood", neighborhood);
            db.update("reports", reports, "author_user_id = ? AND author_anonymous = 0", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful(); return true;
        } finally { db.endTransaction(); }
    }
    private User read(Cursor c) {
        return new User(c.getLong(c.getColumnIndexOrThrow("id")), c.getString(c.getColumnIndexOrThrow("name")),
            c.getString(c.getColumnIndexOrThrow("email")), c.getString(c.getColumnIndexOrThrow("dni")), c.getString(c.getColumnIndexOrThrow("neighborhood")));
    }
}
