package com.example.project_caxfix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.project_caxfix.databinding.BottomSheetNewReportBinding;

public final class NewReportBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "NewReportBottomSheet";
    public static final String[] CATEGORIES = {"Baches y Pistas", "Alumbrado Público", "Limpieza y Residuos", "Seguridad Ciudadana", "Agua y Saneamiento", "Parques y Jardines", "Ordenamiento de Tránsito"};
    private BottomSheetNewReportBinding binding;
    private Uri selectedImage;
    private NewReportViewModel model;
    private final ActivityResultLauncher<String[]> picker = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri != null) {
            selectedImage = uri;
            if (model != null) model.selectedImage = uri;
            try { requireContext().getContentResolver().takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION); }
            catch (SecurityException ignored) { }
            if (binding != null) binding.btnAttachPhoto.setText("Foto adjuntada");
        }
    });
    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        model = new androidx.lifecycle.ViewModelProvider(this).get(NewReportViewModel.class);
        if (state != null && state.getString("image_uri") != null) selectedImage = Uri.parse(state.getString("image_uri"));
        if (model.selectedImage != null) selectedImage = model.selectedImage; else model.selectedImage = selectedImage;
    }
    @Override public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state); if (selectedImage != null) state.putString("image_uri", selectedImage.toString());
    }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        binding = BottomSheetNewReportBinding.inflate(inflater, container, false); return binding.getRoot();
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        model.state.observe(getViewLifecycleOwner(), result -> {
            if (binding == null) return;
            setCancelable(!result.saving);
            binding.btnSubmitReport.setEnabled(!result.saving);
            binding.btnAttachPhoto.setEnabled(!result.saving);
            if (result.saved && !model.resultConsumed) {
                model.resultConsumed = true;
                Toast.makeText(requireContext(), "Reporte publicado", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().setFragmentResult("report_created", new Bundle());
                dismissAllowingStateLoss();
            } else if (result.error != null) {
                Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show();
            }
        });
        binding.actCategory.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CATEGORIES));
        if (selectedImage != null) binding.btnAttachPhoto.setText("Foto adjuntada");
        binding.btnAttachPhoto.setOnClickListener(v -> picker.launch(new String[]{"image/*"}));
        binding.btnSubmitReport.setOnClickListener(v -> submit());
    }
    private void submit() {
        if (model.state.getValue() != null && model.state.getValue().saving) return;
        String title = binding.etReportTitle.getText().toString().trim();
        String category = binding.actCategory.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        if (title.length() < 5 || title.length() > 120 || description.length() < 10 || description.length() > 2000) {
            Toast.makeText(requireContext(), "Ingresa un título de al menos 5 caracteres y una descripción de al menos 10", Toast.LENGTH_LONG).show(); return;
        }
        if (!java.util.Arrays.asList(CATEGORIES).contains(category)) { binding.actCategory.setError("Selecciona una categoría"); return; }
        model.selectedImage = selectedImage;
        model.submit(requireContext(), new SessionManager(requireContext()).userId(), title, category, description);
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
