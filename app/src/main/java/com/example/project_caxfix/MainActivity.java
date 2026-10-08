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
        // Conserva los controles del diseño; las integraciones pendientes se explican al pulsar.
        binding.tvForgotPassword.setOnClickListener(v -> pending("Recuperación de contraseña", "Esta versión guarda cuentas en el dispositivo. Para recuperar contraseñas por correo hace falta conectar un servicio de autenticación."));
        binding.btnDniKey.setOnClickListener(v -> pending("DNI Electrónico / Clave Perú", "La integración de identidad digital aún no está conectada. Puedes ingresar con el DNI y la contraseña que registraste en esta app."));
        binding.btnGoogle.setOnClickListener(v -> pending("Continuar con Google", "El acceso con Google requiere configurar un proveedor de autenticación y sus credenciales. Por ahora usa una cuenta creada en esta app."));
        binding.tvCredits.setOnClickListener(v -> {
            try (java.io.InputStream input = getResources().openRawResource(R.raw.creditos_imagenes)) {
                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                byte[] bytes = new byte[4096]; int count;
                while ((count = input.read(bytes)) > 0) buffer.write(bytes, 0, count);
                pending("Créditos de imágenes", buffer.toString("UTF-8"));
            } catch (java.io.IOException error) { toast("No se pudieron abrir los créditos"); }
        });
        work(() -> new com.example.project_caxfix.data.ReportDao(this).getAll(0, false), reports -> {
            int resolved = 0; java.util.Set<String> neighborhoods = new java.util.HashSet<>();
            for (Report r : reports) { if ("Resuelto".equals(r.status)) resolved++; neighborhoods.add(r.neighborhood); }
            binding.tvWelcomeResolved.setText(String.valueOf(resolved));
            binding.tvWelcomeNeighborhoods.setText(String.valueOf(neighborhoods.size()));
        });
        binding.llRegisterContainer.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        binding.btnLogin.setOnClickListener(v -> login());
        if (session.userId() > 0) {
            work(() -> new UserDao(this).get(session.userId()), user -> {
                if (user != null) openHome(); else session.logout();
            });
        }
    }
    private void pending(String title, String message) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(title).setMessage(message).setPositiveButton("Entendido", null).show();
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
