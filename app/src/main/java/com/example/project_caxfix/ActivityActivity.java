package com.example.project_caxfix;
// Reutiliza el listado y sus filtros mostrando únicamente los reportes del usuario.
public final class ActivityActivity extends HomeActivity {
    @Override protected boolean ownOnly() { return true; }
}
