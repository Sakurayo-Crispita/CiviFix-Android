package com.example.project_caxfix;
import android.content.Intent;
import android.os.Bundle;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.data.ReportDao;
import com.example.project_caxfix.databinding.ActivityProfileBinding;
import com.example.project_caxfix.compose.ProfileSummary;

public final class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (!requireSession()) return;
        binding = ActivityProfileBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot()); applyInsets(binding.getRoot());
        Navigation.bind(this, binding.bottomNavigation, R.id.nav_profile);
        binding.btnSaveProfile.setOnClickListener(v -> save());
        binding.btnLogout.setOnClickListener(v -> {
            session.logout(); startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish();
        });
    }
    @Override protected void onResume() {
        super.onResume(); if (binding == null || !requireSession()) return;
        Navigation.bind(this, binding.bottomNavigation, R.id.nav_profile);
        work(() -> new UserDao(this).get(session.userId()), user -> {
            if (user == null) { session.logout(); requireSession(); return; }
            binding.etName.setText(user.name); binding.etNeighborhood.setText(user.neighborhood);
            binding.tvIdentity.setText(user.email + "\nDNI: " + user.dni);
        });
        work(() -> new ReportDao(this).getAll(session.userId(), true), reports -> {
            int pending = 0, attention = 0, resolved = 0;
            for (Report report : reports) {
                if ("Resuelto".equals(report.status)) resolved++;
                else if ("En Atención".equals(report.status)) attention++;
                else pending++;
            }
            ProfileSummary.mount(binding.composeSummary, reports.size(), pending, attention, resolved);
        });
    }
    private void save() {
        String name = binding.etName.getText().toString().trim(); String neighborhood = binding.etNeighborhood.getText().toString().trim();
        if (name.length() < 3 || neighborhood.isEmpty()) { toast("Completa tu nombre y barrio"); return; }
        binding.btnSaveProfile.setEnabled(false);
        work(() -> new UserDao(this).update(session.userId(), name, neighborhood), done -> {
            binding.btnSaveProfile.setEnabled(true); toast(done ? "Perfil actualizado" : "No se encontró la cuenta");
        }, error -> { binding.btnSaveProfile.setEnabled(true); toast(error); });
    }
}
