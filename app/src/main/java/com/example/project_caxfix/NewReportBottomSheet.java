package com.example.project_caxfix;

import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.project_caxfix.databinding.BottomSheetNewReportBinding;

/** Formulario Java/XML. El ViewModel mantiene la publicación al girar el teléfono. */
public final class NewReportBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "NewReportBottomSheet";
    public static final String[] CATEGORIES = {"Baches y Pistas", "Alumbrado Público", "Limpieza y Residuos", "Seguridad Ciudadana", "Agua y Saneamiento", "Parques y Jardines", "Ordenamiento de Tránsito"};
    private BottomSheetNewReportBinding binding;
    private Uri selectedImage;
    private NewReportViewModel model;
    private SharedPreferences draft() {
        return requireContext().getSharedPreferences("report_draft_" + new SessionManager(requireContext()).userId(), Context.MODE_PRIVATE);
    }
    private final ActivityResultLauncher<String[]> picker = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri == null) return;
        selectedImage = uri; if (model != null) model.selectedImage = uri;
        try { requireContext().getContentResolver().takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION); }
        catch (SecurityException ignored) { }
        showPhoto();
    });
    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state); model = new androidx.lifecycle.ViewModelProvider(this).get(NewReportViewModel.class);
        if (state != null && state.getString("image_uri") != null) selectedImage = Uri.parse(state.getString("image_uri"));
        if (model.selectedImage != null) selectedImage = model.selectedImage; else model.selectedImage = selectedImage;
    }
    @Override public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state); if (selectedImage != null) state.putString("image_uri", selectedImage.toString());
    }
    @Override public void onStart() {
        super.onStart();
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) { sheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT; sheet.requestLayout(); }
            dialog.getBehavior().setSkipCollapsed(true); dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        binding = BottomSheetNewReportBinding.inflate(inflater, container, false); return binding.getRoot();
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        binding.bottomNavigation.setItemActiveIndicatorEnabled(false);
        binding.bottomNavigation.setSelectedItemId(R.id.nav_new);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_new) return false;
            Class<?> target = item.getItemId() == R.id.nav_profile ? ProfileActivity.class : item.getItemId() == R.id.nav_activity ? ActivityActivity.class : HomeActivity.class;
            dismiss(); startActivity(new android.content.Intent(requireContext(), target).addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)); return true;
        });
        binding.fabNewReport.setOnClickListener(v -> binding.etReportTitle.requestFocus());
        binding.ivUserProfile.setOnClickListener(v -> { startActivity(new android.content.Intent(requireContext(), ProfileActivity.class)); dismiss(); });
        binding.categoryChips.setOnCheckedStateChangeListener((group, ids) -> {
            Chip chip = group.findViewById(group.getCheckedChipId());
            if (chip != null) binding.actCategory.setText(chip.getText().toString());
        });
        if (state == null) restoreDraft();
        showPhoto();
        binding.btnAttachPhoto.setOnClickListener(v -> picker.launch(new String[]{"image/*"}));
        binding.btnSubmitReport.setOnClickListener(v -> submit());
        binding.btnSaveDraft.setOnClickListener(v -> {
            draft().edit().putString("title", binding.etReportTitle.getText().toString())
                .putString("description", binding.etDescription.getText().toString())
                .putString("location", binding.etLocation.getText().toString())
                .putString("category", binding.actCategory.getText().toString())
                .putBoolean("anonymous", binding.switchAnonymous.isChecked())
                .putString("image", selectedImage == null ? null : selectedImage.toString()).apply();
            Toast.makeText(requireContext(), "Borrador guardado en este dispositivo", Toast.LENGTH_SHORT).show(); dismiss();
        });
        binding.btnDiscard.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Descartar reporte?").setMessage("Se eliminará el borrador guardado de esta cuenta.")
            .setNegativeButton("Seguir editando", null).setPositiveButton("Descartar", (d, w) -> { draft().edit().clear().apply(); dismiss(); }).show());
        binding.btnAdjustMap.setOnClickListener(v -> {
            String address = binding.etLocation.getText().toString().trim();
            Uri uri = Uri.parse("geo:-7.157,-78.5176?q=" + Uri.encode(address.isEmpty() ? "Cajamarca, Perú" : address + ", Cajamarca, Perú"));
            try { startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, uri)); }
            catch (android.content.ActivityNotFoundException error) { Toast.makeText(requireContext(), "Instala una aplicación de mapas para abrir la ubicación", Toast.LENGTH_LONG).show(); }
        });
        model.state.observe(getViewLifecycleOwner(), result -> {
            if (binding == null) return;
            setCancelable(!result.saving); binding.btnSubmitReport.setEnabled(!result.saving);
            binding.btnAttachPhoto.setEnabled(!result.saving); binding.btnSaveDraft.setEnabled(!result.saving); binding.btnDiscard.setEnabled(!result.saving);
            if (result.saved && !model.resultConsumed) {
                model.resultConsumed = true; draft().edit().clear().apply();
                Toast.makeText(requireContext(), "Reporte publicado", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().setFragmentResult("report_created", new Bundle()); dismissAllowingStateLoss();
            } else if (result.error != null) Toast.makeText(requireContext(), result.error, Toast.LENGTH_LONG).show();
        });
    }
    private void restoreDraft() {
        SharedPreferences saved = draft();
        binding.etReportTitle.setText(saved.getString("title", "")); binding.etDescription.setText(saved.getString("description", ""));
        binding.etLocation.setText(saved.getString("location", "")); binding.switchAnonymous.setChecked(saved.getBoolean("anonymous", false));
        String category = saved.getString("category", "Baches y Pistas");
        for (int i = 0; i < binding.categoryChips.getChildCount(); i++) {
            View child = binding.categoryChips.getChildAt(i);
            if (child instanceof Chip && ((Chip) child).getText().toString().equals(category)) binding.categoryChips.check(child.getId());
        }
        String image = saved.getString("image", null); if (selectedImage == null && image != null) selectedImage = Uri.parse(image);
        model.selectedImage = selectedImage;
    }
    private void showPhoto() {
        if (binding == null) return;
        ImageLoader.load(binding.imgPhotoPreview, selectedImage == null ? null : selectedImage.toString());
        binding.btnAttachPhoto.setText(selectedImage == null ? "Adjuntar foto" : "Cambiar foto");
        binding.tvPhotoName.setText(selectedImage == null ? "Sin foto seleccionada" : "1 foto seleccionada · se guardará con el reporte");
    }
    private void submit() {
        if (model.state.getValue() != null && model.state.getValue().saving) return;
        String title = binding.etReportTitle.getText().toString().trim(); String category = binding.actCategory.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim(); String location = binding.etLocation.getText().toString().trim();
        if (title.length() < 5 || title.length() > 60) { binding.tilReportTitle.setError("Usa entre 5 y 60 caracteres"); return; }
        binding.tilReportTitle.setError(null);
        if (description.length() < 20 || description.length() > 2000) { binding.tilDescription.setError("Usa entre 20 y 2000 caracteres"); return; }
        binding.tilDescription.setError(null);
        if (!java.util.Arrays.asList(CATEGORIES).contains(category)) { Toast.makeText(requireContext(), "Selecciona una categoría", Toast.LENGTH_SHORT).show(); return; }
        if (location.isEmpty()) { binding.etLocation.setError("Escribe la dirección o una referencia"); return; }
        model.selectedImage = selectedImage;
        model.submit(requireContext(), new SessionManager(requireContext()).userId(), title, category, description, location, binding.switchAnonymous.isChecked());
    }
    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
