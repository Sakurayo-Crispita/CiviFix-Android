package com.example.project_caxfix;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public final class ImageLoader {
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private ImageLoader() {}
    public static void load(ImageView view, String uri) {
        view.setTag(uri); view.setImageDrawable(null);
        if (uri == null || uri.isEmpty()) { view.setVisibility(View.GONE); return; }
        view.setVisibility(View.VISIBLE);
        Context context = view.getContext().getApplicationContext();
        IO.execute(() -> {
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options(); options.inJustDecodeBounds = true;
                try (InputStream in = context.getContentResolver().openInputStream(Uri.parse(uri))) { BitmapFactory.decodeStream(in, null, options); }
                options.inSampleSize = 1;
                while (Math.max(options.outWidth, options.outHeight) / options.inSampleSize > 1200) options.inSampleSize *= 2;
                options.inJustDecodeBounds = false;
                try (InputStream in = context.getContentResolver().openInputStream(Uri.parse(uri))) { bitmap = BitmapFactory.decodeStream(in, null, options); }
            } catch (Exception e) { android.util.Log.w("CiviFix", "Imagen no disponible", e); }
            Bitmap result = bitmap;
            view.post(() -> { if (uri.equals(view.getTag())) {
                view.setImageBitmap(result); view.setVisibility(result == null ? View.GONE : View.VISIBLE);
            } });
        });
    }
}
