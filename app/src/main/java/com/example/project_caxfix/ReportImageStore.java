package com.example.project_caxfix;
import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;

public final class ReportImageStore {
    private ReportImageStore() {}
    public static String copy(Context context, Uri uri) throws IOException {
        if (uri == null) return null;
        File dir = new File(context.getFilesDir(), "report_images");
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("No se pudo crear la carpeta de fotos");
        File file = new File(dir, UUID.randomUUID() + ".img");
        try (InputStream in = context.getContentResolver().openInputStream(uri); FileOutputStream out = new FileOutputStream(file)) {
            if (in == null) throw new IOException("No se pudo abrir la imagen");
            byte[] buffer = new byte[8192]; int read; long total = 0;
            while ((read = in.read(buffer)) != -1) {
                total += read; if (total > 16L * 1024 * 1024) throw new IOException("La imagen supera 16 MB");
                out.write(buffer, 0, read);
            }
        } catch (Exception e) { file.delete(); throw new IOException("No se pudo guardar la foto. Usa una imagen de hasta 16 MB.", e); }
        return Uri.fromFile(file).toString();
    }
    public static void delete(Context context, String uri) {
        if (uri == null) return;
        try {
            File file = new File(Uri.parse(uri).getPath());
            File folder = new File(context.getFilesDir(), "report_images");
            if (file.getCanonicalFile().getParentFile().equals(folder.getCanonicalFile())) file.delete();
        } catch (Exception ignored) { }
    }
}
