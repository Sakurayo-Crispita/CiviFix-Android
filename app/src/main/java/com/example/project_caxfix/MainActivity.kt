package com.example.project_caxfix

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_caxfix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar listener para el botón de inicio de sesión
        binding.btnLogin.setOnClickListener {
            val emailOrDni = binding.etEmailOrDni.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (emailOrDni.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // ¿Olvidaste tu contraseña?
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Recuperación de contraseña disponible próximamente", Toast.LENGTH_SHORT).show()
        }

        // DNI Electrónico / Clave Perú
        binding.btnDniKey.setOnClickListener {
            Toast.makeText(this, "Acceso con DNI Electrónico / Clave Perú en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Continuar con Google
        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Inicio de sesión con Google en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Regístrate como vecino de Cajamarca
        binding.llRegisterContainer.setOnClickListener {
            Toast.makeText(this, "Registro de nuevos vecinos en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }
}