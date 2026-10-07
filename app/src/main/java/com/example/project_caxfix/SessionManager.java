package com.example.project_caxfix;
import android.content.Context;
import android.content.SharedPreferences;
public final class SessionManager {
    private final SharedPreferences prefs;
    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences("civifix_session", Context.MODE_PRIVATE);
    }
    public long userId() { return prefs.getLong("user_id", -1); }
    public void login(long id) { prefs.edit().putLong("user_id", id).apply(); }
    public void logout() { prefs.edit().clear().apply(); }
}
