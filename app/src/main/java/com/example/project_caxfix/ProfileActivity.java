package com.example.project_caxfix;
import android.content.Intent;
import android.os.Bundle;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.data.ReportDao;
import com.example.project_caxfix.databinding.ActivityProfileBinding;
import com.example.project_caxfix.compose.ProfileSummary;

public final class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;
    private android.content.SharedPreferences preferences() { return getSharedPreferences("profile_" + session.userId(), MODE_PRIVATE); }
    private final androidx.activity.result.ActivityResultLauncher<String[]> photoPicker = registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.OpenDocument(), uri -> {
        if (uri == null) return;
        work(() -> ReportImageStore.copy(this, uri), path -> {
            String old = preferences().getString("photo", null);
            preferences().edit().putString("photo", path).apply();
            ReportImageStore.delete(this, old); loadPhoto();
        });
    });
    private void loadPhoto() {
        String uri = preferences().getString("photo", null);
        if (uri == null) { int padding = (int) (28 * getResources().getDisplayMetrics().density); binding.imgProfile.setImageResource(R.drawable.ic_figma_user); binding.imgProfile.setVisibility(android.view.View.VISIBLE); binding.imgProfile.setPadding(padding,padding,padding,padding); }
        else { binding.imgProfile.setPadding(0,0,0,0); ImageLoader.load(binding.imgProfile, uri); }
    }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (!requireSession()) return;
        binding = ActivityProfileBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot()); applyInsets(binding.getRoot());
        Navigation.bind(this, binding.bottomNavigation, R.id.nav_profile);
        binding.fabNewReport.setOnClickListener(v -> Navigation.showNewReport(this));
        binding.imgProfile.setOnClickListener(v -> photoPicker.launch(new String[]{"image/*"}));
        binding.tvChangePhoto.setOnClickListener(v -> photoPicker.launch(new String[]{"image/*"}));
        binding.ivUserProfile.setOnClickListener(v -> binding.imgProfile.performClick());
        binding.tvAllReports.setOnClickListener(v -> startActivity(new Intent(this, ActivityActivity.class)));
        getSupportFragmentManager().setFragmentResultListener("report_created", this, (key, result) -> refresh());
        binding.btnSaveProfile.setOnClickListener(v -> save());
        binding.btnLogout.setOnClickListener(v -> {
            session.logout(); startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish();
        });
    }
    @Override protected void onResume() {
        super.onResume(); if (binding == null || !requireSession()) return;
        Navigation.bind(this, binding.bottomNavigation, R.id.nav_profile); refresh();
    }
    private void refresh() {
        binding.etPhone.setText(preferences().getString("phone", ""));
        binding.switchProgress.setChecked(preferences().getBoolean("progress_alerts", false));
        binding.switchUrgent.setChecked(preferences().getBoolean("urgent_alerts", false)); loadPhoto();
        work(() -> new UserDao(this).get(session.userId()), user -> {
            if (user == null) { session.logout(); requireSession(); return; }
            binding.etName.setText(user.name); binding.etNeighborhood.setText(user.neighborhood);
            binding.tvIdentity.setText(user.email + "\nDNI: " + user.dni);
        });
        work(() -> new ReportDao(this).getAll(session.userId(), true), reports -> {
            int resolved = 0;
            for (Report report : reports) if ("Resuelto".equals(report.status)) resolved++;
            final int solved = resolved;
            work(() -> new ReportDao(this).supportsGiven(session.userId()), supports -> ProfileSummary.mount(binding.composeSummary, reports.size(), solved, supports));
            binding.recentReports.removeAllViews();
            if (reports.isEmpty()) { android.widget.TextView empty = new android.widget.TextView(this); empty.setText("Aún no tienes reportes. Usa + para crear uno."); binding.recentReports.addView(empty); }
            for (int i = 0; i < Math.min(2, reports.size()); i++) {
                Report report = reports.get(i);
                com.google.android.material.button.MaterialButton item = new com.google.android.material.button.MaterialButton(this);
                item.setText(report.title + "\n" + report.status + " · " + report.getFormattedTimeAgo());
                item.setTextColor(android.graphics.Color.parseColor("#111C2D")); item.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
                item.setTextAlignment(android.view.View.TEXT_ALIGNMENT_VIEW_START); item.setAllCaps(false);
                item.setOnClickListener(v -> startActivity(new Intent(this, DetailActivity.class).putExtra("report_id", report.id)));
                binding.recentReports.addView(item);
            }
        });
    }
    private void save() {
        String name = binding.etName.getText().toString().trim(); String neighborhood = binding.etNeighborhood.getText().toString().trim();
        if (name.length() < 3 || neighborhood.isEmpty()) { toast("Completa tu nombre y barrio"); return; }
        binding.btnSaveProfile.setEnabled(false);
        work(() -> new UserDao(this).update(session.userId(), name, neighborhood), done -> {
            binding.btnSaveProfile.setEnabled(true);
            if (done) preferences().edit().putString("phone", binding.etPhone.getText().toString().trim()).putBoolean("progress_alerts", binding.switchProgress.isChecked()).putBoolean("urgent_alerts", binding.switchUrgent.isChecked()).apply();
            toast(done ? "Perfil actualizado" : "No se encontró la cuenta");
        }, error -> { binding.btnSaveProfile.setEnabled(true); toast(error); });
    }
}
