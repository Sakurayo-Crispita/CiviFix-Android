package com.example.project_caxfix;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.project_caxfix.data.UserDao;
import com.example.project_caxfix.databinding.ActivityMainBinding;

public final class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); setContentView(binding.getRoot()); applyInsets(binding.getRoot());
        binding.tvSubtitle.setText("Ingresa con una cuenta creada en este dispositivo.");
        // Estas integraciones externas no forman parte de la versión local.
        binding.tvForgotPassword.setVisibility(View.GONE);
        binding.btnDniKey.setVisibility(View.GONE); binding.btnGoogle.setVisibility(View.GONE); binding.tvDividerText.setVisibility(View.GONE);
        binding.llRegisterContainer.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        binding.btnLogin.setOnClickListener(v -> login());
        if (session.userId() > 0) {
            work(() -> new UserDao(this).get(session.userId()), user -> {
                if (user != null) openHome(); else session.logout();
            });
        }
    }
    private void login() {
        String identifier = binding.etEmailOrDni.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        binding.tilEmailOrDni.setError(null); binding.tilPassword.setError(null);
        if (identifier.isEmpty()) { binding.tilEmailOrDni.setError("Ingresa tu correo o DNI"); return; }
        if (password.isEmpty()) { binding.tilPassword.setError("Ingresa tu contraseña"); return; }
        binding.btnLogin.setEnabled(false);
        work(() -> new UserDao(this).login(identifier, password), user -> {
            binding.btnLogin.setEnabled(true);
            if (user == null) { binding.tilPassword.setError("Correo, DNI o contraseña incorrectos"); return; }
            session.login(user.id); openHome();
        }, error -> { binding.btnLogin.setEnabled(true); toast(error); });
    }
    private void openHome() {
        startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish();
    }
}
