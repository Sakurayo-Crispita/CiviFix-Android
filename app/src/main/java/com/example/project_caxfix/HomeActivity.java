package com.example.project_caxfix;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import com.example.project_caxfix.data.ReportDao;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.databinding.ActivityHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private ReportAdapter adapter;
    private List<Report> reports = new ArrayList<>();
    private boolean toggling;
    protected boolean ownOnly() { return false; }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (!requireSession()) return;
        binding = ActivityHomeBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot()); applyInsets(binding.getRoot());
        adapter = new ReportAdapter(new ReportAdapter.Listener() {
            @Override public void onOpen(Report report) {
                startActivity(new Intent(HomeActivity.this, DetailActivity.class).putExtra("report_id", report.id));
            }
            @Override public void onSupport(Report report) {
                if (toggling) return; toggling = true;
                work(() -> { new ReportDao(HomeActivity.this).toggleSupport(report.id, session.userId()); return true; },
                    done -> { toggling = false; load(); }, error -> { toggling = false; toast(error); });
            }
        });
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(this)); binding.rvFeed.setAdapter(adapter);
        binding.chipGroupFilters.setOnCheckedStateChangeListener((group, ids) -> filter());
        Navigation.bind(this, binding.bottomNavigation, ownOnly() ? R.id.nav_activity : R.id.nav_reports);
        binding.ivUserProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.fabNewReport.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentByTag(NewReportBottomSheet.TAG) == null)
                new NewReportBottomSheet().show(getSupportFragmentManager(), NewReportBottomSheet.TAG);
        });
        getSupportFragmentManager().setFragmentResultListener("report_created", this, (key, result) -> load());
    }
    @Override protected void onResume() {
        super.onResume(); if (binding != null && requireSession()) {
            Navigation.bind(this, binding.bottomNavigation, ownOnly() ? R.id.nav_activity : R.id.nav_reports); load();
        }
    }
    private void load() {
        work(() -> new UserDao(this).get(session.userId()), user -> {
            if (user == null) { session.logout(); requireSession(); return; }
            binding.tvLocationName.setText(ownOnly() ? "Mi actividad" : user.neighborhood);
        });
        work(() -> new ReportDao(this).getAll(session.userId(), ownOnly()), result -> { reports = result; filter(); });
    }
    private void filter() {
        int id = binding.chipGroupFilters.getCheckedChipId();
        Chip chip = binding.chipGroupFilters.findViewById(id);
        String category = chip == null ? "Todos" : chip.getText().toString();
        List<Report> filtered = new ArrayList<>();
        for (Report report : reports) if (id == View.NO_ID || id == binding.chipAll.getId() || category.equalsIgnoreCase(report.category)) filtered.add(report);
        adapter.updateReports(filtered);
        binding.tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tvEmpty.setText(ownOnly() ? "Todavía no tienes reportes en esta categoría. Usa + para crear uno." : "No hay reportes en esta categoría.");
    }
}
