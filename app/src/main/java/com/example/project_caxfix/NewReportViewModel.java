package com.example.project_caxfix;
import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.data.ReportDao;

// Mantiene la publicación en curso si se gira la pantalla.
public final class NewReportViewModel extends ViewModel {
    public final MutableLiveData<State> state = new MutableLiveData<>(new State(false, false, null));
    public Uri selectedImage;
    public boolean resultConsumed;
    public void submit(Context context, long userId, String title, String category, String description) {
        State current = state.getValue(); if (current != null && (current.saving || current.saved)) return;
        Context app = context.getApplicationContext(); Uri image = selectedImage;
        state.setValue(new State(true, false, null));
        AppExecutors.IO.execute(() -> {
            String copied = null;
            try {
                User user = new UserDao(app).get(userId);
                if (user == null) throw new IllegalStateException("Sesión no disponible");
                copied = ReportImageStore.copy(app, image);
                new ReportDao(app).insert(user, title, category, description, copied);
                state.postValue(new State(false, true, null));
            } catch (Exception e) {
                ReportImageStore.delete(app, copied);
                android.util.Log.e("CiviFix", "Error al publicar reporte", e);
                state.postValue(new State(false, false, "No se pudo publicar. Revisa la foto (máximo 16 MB) e intenta nuevamente."));
            }
        });
    }
    public static final class State {
        public final boolean saving, saved; public final String error;
        State(boolean saving, boolean saved, String error) { this.saving = saving; this.saved = saved; this.error = error; }
    }
}
