package com.example.project_caxfix;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public final class AppExecutors {
    // SQLite y el cálculo de contraseñas se ejecutan fuera del hilo de la interfaz.
    public static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private AppExecutors() {}
}
