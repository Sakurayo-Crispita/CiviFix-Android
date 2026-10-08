package com.example.project_caxfix;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_caxfix.databinding.ItemReportBinding;
import java.util.ArrayList;
import java.util.List;

public final class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.Holder> {
    public interface Listener { void onOpen(Report report); void onSupport(Report report); void onNewReport(); }
    private List<Report> reports = new ArrayList<>();
    private final Listener listener;
    public ReportAdapter(Listener listener) { this.listener = listener; }
    public void updateReports(List<Report> reports) { this.reports = new ArrayList<>(reports); notifyDataSetChanged(); }
    public static final class Holder extends RecyclerView.ViewHolder {
        final ItemReportBinding binding;
        Holder(ItemReportBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemReportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        Report r = reports.get(position); ItemReportBinding b = holder.binding;
        b.tvAuthorName.setText(r.authorName); b.tvNeighborhoodAndTime.setText(r.neighborhood + " • " + r.getFormattedTimeAgo());
        b.tvStatusBadge.setText(r.status); b.tvReportTitle.setText(r.title); b.tvReportDescription.setText(r.description);
        b.tvSupportCount.setText((r.supported ? "✓ " : "") + r.supportCount + " apoyos comunitarios");
        b.tvCommentCount.setText(r.commentCount + " comentarios");
        b.tvSupportCount.setContentDescription(r.supported ? "Retirar mi apoyo" : "Apoyar reporte");
        b.tvSupportCount.setOnClickListener(v -> listener.onSupport(r));
        b.tvCommentCount.setOnClickListener(v -> listener.onOpen(r)); b.getRoot().setOnClickListener(v -> listener.onOpen(r));
        b.btnSupport.setText(r.supported ? "Apoyado ✓" : "Apoyar");
        b.btnSupport.setOnClickListener(v -> listener.onSupport(r));
        b.btnComment.setOnClickListener(v -> listener.onOpen(r));
        b.btnNewReport.setOnClickListener(v -> listener.onNewReport());
        b.btnShare.setOnClickListener(v -> {
            android.content.Intent share = new android.content.Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain"); share.putExtra(android.content.Intent.EXTRA_TEXT, "CiviFix · " + r.title + "\n" + r.description + "\nBarrio: " + r.neighborhood + "\nEstado: " + r.status);
            v.getContext().startActivity(android.content.Intent.createChooser(share, "Compartir reporte"));
        });
        boolean resolved = "Resuelto".equals(r.status), attention = "En Atención".equals(r.status);
        b.reportProgress.setProgress(resolved ? 100 : attention ? 50 : 25);
        b.tvStatusBadge.setText(resolved ? "Solucionado" : r.status);
        android.graphics.drawable.GradientDrawable badge = new android.graphics.drawable.GradientDrawable();
        badge.setCornerRadius(100 * b.getRoot().getResources().getDisplayMetrics().density);
        badge.setColor(android.graphics.Color.parseColor(resolved ? "#AEF0D7" : "#FFDBC3"));
        b.tvStatusBadge.setBackground(badge);
        b.tvStatusBadge.setTextColor(android.graphics.Color.parseColor(resolved ? "#004635" : "#904D00"));
        ImageLoader.load(b.ivReportImage, r.imageUri);
    }
    @Override public int getItemCount() { return reports.size(); }
}
