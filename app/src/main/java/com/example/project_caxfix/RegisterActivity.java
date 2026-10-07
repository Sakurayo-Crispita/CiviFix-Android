package com.example.project_caxfix;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.databinding.ActivityRegisterBinding;

public final class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); applyInsets(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish()); binding.btnRegister.setOnClickListener(v -> register());
    }
    private void register() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String dni = binding.etDni.getText().toString().trim();
        String neighborhood = binding.etNeighborhood.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String confirmation = binding.etConfirmPassword.getText().toString();
        if (name.length() < 3) { binding.etName.setError("Ingresa tu nombre completo"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.etEmail.setError("Correo inválido"); return; }
        if (!dni.matches("[0-9]{8}")) { binding.etDni.setError("El DNI debe tener 8 dígitos"); return; }
        if (neighborhood.isEmpty()) { binding.etNeighborhood.setError("Ingresa tu barrio"); return; }
        if (password.length() < 8) { binding.etPassword.setError("Usa al menos 8 caracteres"); return; }
        if (!password.equals(confirmation)) { binding.etConfirmPassword.setError("Las contraseñas no coinciden"); return; }
        binding.btnRegister.setEnabled(false);
        work(() -> new UserDao(this).register(name, email, dni, neighborhood, password), id -> {
            binding.btnRegister.setEnabled(true);
            if (id < 0) { toast("El correo o DNI ya está registrado"); return; }
            session.login(id);
            startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish();
        }, error -> { binding.btnRegister.setEnabled(true); toast(error); });
    }
}
