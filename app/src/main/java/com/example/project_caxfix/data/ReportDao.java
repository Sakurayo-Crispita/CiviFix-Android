package com.example.project_caxfix.data;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.project_caxfix.Report;
import com.example.project_caxfix.User;
import com.example.project_caxfix.Comment;
import java.util.ArrayList;
import java.util.List;

public final class ReportDao {
    private final CiviFixDatabaseHelper helper;
    public ReportDao(Context context) { helper = CiviFixDatabaseHelper.get(context); }
    public long insert(User user, String title, String category, String description, String imageUri) {
        ContentValues v = new ContentValues();
        v.put("author_user_id", user.id); v.put("author_name", user.name); v.put("neighborhood", user.neighborhood);
        v.put("status", "Pendiente"); v.put("title", title); v.put("category", category); v.put("description", description);
        v.put("image_uri", imageUri); v.put("created_at", System.currentTimeMillis());
        return helper.getWritableDatabase().insertOrThrow("reports", null, v);
    }
    public List<Report> getAll(long userId, boolean ownOnly) {
        String sql = "SELECT r.*, EXISTS(SELECT 1 FROM supports s WHERE s.report_id = r.id AND s.user_id = ?) AS supported FROM reports r";
        String[] args = ownOnly ? new String[]{String.valueOf(userId), String.valueOf(userId)} : new String[]{String.valueOf(userId)};
        if (ownOnly) sql += " WHERE r.author_user_id = ?";
        sql += " ORDER BY r.created_at DESC, r.id DESC";
        List<Report> out = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, args)) { while (c.moveToNext()) out.add(read(c)); }
        return out;
    }
    public Report get(long reportId, long userId) {
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT r.*, EXISTS(SELECT 1 FROM supports s WHERE s.report_id = r.id AND s.user_id = ?) AS supported FROM reports r WHERE r.id = ?", new String[]{String.valueOf(userId), String.valueOf(reportId)})) {
            return c.moveToFirst() ? read(c) : null;
        }
    }
    public void toggleSupport(long reportId, long userId) {
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            String[] args = {String.valueOf(reportId), String.valueOf(userId)};
            int removed = db.delete("supports", "report_id = ? AND user_id = ?", args);
            if (removed == 0) {
                ContentValues v = new ContentValues(); v.put("report_id", reportId); v.put("user_id", userId);
                db.insertOrThrow("supports", null, v);
                db.execSQL("UPDATE reports SET support_count = COALESCE(support_count, 0) + 1 WHERE id = ?", new Object[]{reportId});
            } else db.execSQL("UPDATE reports SET support_count = MAX(0, COALESCE(support_count, 0) - 1) WHERE id = ?", new Object[]{reportId});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public List<Comment> comments(long reportId) {
        List<Comment> out = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery("SELECT u.name, c.body, c.created_at FROM comments c JOIN users u ON u.id = c.user_id WHERE c.report_id = ? ORDER BY c.created_at, c.id", new String[]{String.valueOf(reportId)})) {
            while (c.moveToNext()) out.add(new Comment(c.getString(0), c.getString(1), c.getLong(2)));
        }
        return out;
    }
    public void addComment(long reportId, long userId, String body) {
        if (body.trim().isEmpty() || body.length() > 1000) throw new IllegalArgumentException("Comentario inválido");
        SQLiteDatabase db = helper.getWritableDatabase(); db.beginTransaction();
        try {
            ContentValues v = new ContentValues(); v.put("report_id", reportId); v.put("user_id", userId);
            v.put("body", body.trim()); v.put("created_at", System.currentTimeMillis()); db.insertOrThrow("comments", null, v);
            db.execSQL("UPDATE reports SET comment_count = COALESCE(comment_count, 0) + 1 WHERE id = ?", new Object[]{reportId});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public boolean deleteOwned(long reportId, long userId) {
        return helper.getWritableDatabase().delete("reports", "id = ? AND author_user_id = ?", new String[]{String.valueOf(reportId), String.valueOf(userId)}) == 1;
    }
    private Report read(Cursor c) {
        return new Report(number(c,"id"), number(c,"author_user_id"), text(c,"author_name"), text(c,"neighborhood"),
            text(c,"status"), text(c,"title"), text(c,"category"), text(c,"description"), text(c,"image_uri"),
            number(c,"created_at"), (int) number(c,"support_count"), (int) number(c,"comment_count"), number(c,"supported") != 0);
    }
    private String text(Cursor c, String key) { return c.getString(c.getColumnIndexOrThrow(key)); }
    private long number(Cursor c, String key) { return c.getLong(c.getColumnIndexOrThrow(key)); }
}
