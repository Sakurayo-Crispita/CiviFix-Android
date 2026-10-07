package com.example.project_caxfix;
public final class User {
    public final long id;
    public final String name, email, dni, neighborhood;
    public User(long id, String name, String email, String dni, String neighborhood) {
        this.id = id; this.name = name; this.email = email;
        this.dni = dni; this.neighborhood = neighborhood;
    }
}
