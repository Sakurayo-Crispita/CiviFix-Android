package com.example.project_caxfix;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.project_caxfix.data.ReportDao;
import com.example.project_caxfix.databinding.ActivityDetailBinding;
import java.text.DateFormat;
import java.util.Date;

public final class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private long reportId;
    private Report report;
    private boolean supporting, commenting;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (!requireSession()) return;
        binding = ActivityDetailBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot()); applyInsets(binding.getRoot());
        reportId = getIntent().getLongExtra("report_id", -1);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSupport.setOnClickListener(v -> support()); binding.btnComment.setOnClickListener(v -> comment());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());
    }
    @Override protected void onResume() { super.onResume(); if (binding != null && requireSession()) load(); }
    private void load() {
        work(() -> new ReportDao(this).get(reportId, session.userId()), result -> {
            if (result == null) { toast("El reporte ya no está disponible"); finish(); return; }
            report = result;
            binding.tvTitle.setText(report.title); binding.tvAuthor.setText(report.authorName + " • " + report.neighborhood + " • " + report.getFormattedTimeAgo());
            binding.tvCategory.setText(report.category + " • " + report.status); binding.tvDescription.setText(report.description);
            binding.btnSupport.setText((report.supported ? "Retirar apoyo" : "Apoyar reporte") + " (" + report.supportCount + ")");
            binding.tvCommentsTitle.setText("Comentarios (" + report.commentCount + ")");
            binding.btnDelete.setVisibility(report.authorUserId == session.userId() ? View.VISIBLE : View.GONE);
            ImageLoader.load(binding.ivPhoto, report.imageUri);
        });
        work(() -> new ReportDao(this).comments(reportId), comments -> {
            binding.llComments.removeAllViews();
            if (comments.isEmpty()) { TextView empty = new TextView(this); empty.setText("Sé el primero en comentar."); binding.llComments.addView(empty); }
            for (Comment comment : comments) {
                TextView text = new TextView(this); text.setText(comment.authorName + " • " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(comment.createdAt)) + "\n" + comment.body);
                text.setTextSize(15); int padding = (int) (12 * getResources().getDisplayMetrics().density); text.setPadding(0, padding, 0, padding);
                binding.llComments.addView(text);
            }
        });
    }
    private void support() {
        if (supporting || report == null) return;
        supporting = true; binding.btnSupport.setEnabled(false);
        work(() -> { new ReportDao(this).toggleSupport(reportId, session.userId()); return true; }, done -> {
            supporting = false; binding.btnSupport.setEnabled(true); load();
        }, error -> { supporting = false; binding.btnSupport.setEnabled(true); toast(error); });
    }
    private void comment() {
        if (commenting || report == null) return;
        String body = binding.etComment.getText().toString().trim();
        if (body.isEmpty()) { binding.etComment.setError("Escribe un comentario"); return; }
        commenting = true; binding.btnComment.setEnabled(false);
        work(() -> { new ReportDao(this).addComment(reportId, session.userId(), body); return true; }, done -> {
            commenting = false; binding.btnComment.setEnabled(true); binding.etComment.setText(""); load();
        }, error -> { commenting = false; binding.btnComment.setEnabled(true); toast(error); });
    }
    private void confirmDelete() {
        if (report == null || report.authorUserId != session.userId()) return;
        new MaterialAlertDialogBuilder(this).setTitle("Eliminar reporte")
            .setMessage("Se eliminarán este reporte, sus apoyos y sus comentarios.")
            .setNegativeButton("Cancelar", null).setPositiveButton("Eliminar", (dialog, which) -> {
                binding.btnDelete.setEnabled(false); String image = report.imageUri;
                work(() -> {
                    boolean deleted = new ReportDao(this).deleteOwned(reportId, session.userId());
                    if (deleted) ReportImageStore.delete(this, image); return deleted;
                }, deleted -> {
                    binding.btnDelete.setEnabled(true);
                    if (deleted) { toast("Reporte eliminado"); finish(); } else toast("Solo puedes eliminar tus reportes");
                }, error -> { binding.btnDelete.setEnabled(true); toast(error); });
            }).show();
    }
}
