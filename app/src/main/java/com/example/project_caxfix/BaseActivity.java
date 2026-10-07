package com.example.project_caxfix;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class BaseActivity extends AppCompatActivity {
    protected SessionManager session;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); session = new SessionManager(this);
    }
    protected boolean requireSession() {
        if (session.userId() > 0) return true;
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish(); return false;
    }
    protected void applyInsets(View root) {
        int left = root.getPaddingLeft(), top = root.getPaddingTop();
        int right = root.getPaddingRight(), bottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }
    protected <T> void work(Callable<T> task, Consumer<T> success) { work(task, success, message -> toast(message)); }
    protected <T> void work(Callable<T> task, Consumer<T> success, Consumer<String> failure) {
        AppExecutors.IO.execute(() -> {
            try {
                T result = task.call();
                runOnUiThread(() -> { if (!isFinishing() && !isDestroyed()) success.accept(result); });
            } catch (Exception e) {
                android.util.Log.e("CiviFix", "Error de operación", e);
                runOnUiThread(() -> { if (!isFinishing() && !isDestroyed()) failure.accept("No se pudo completar la operación. Intenta nuevamente."); });
            }
        });
    }
    protected void toast(String text) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show(); }
}
