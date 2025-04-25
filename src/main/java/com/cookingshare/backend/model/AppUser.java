package com.cookingshare.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class AppUser {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String provider = "LOCAL"; // or GOOGLE
    private String role = "LEARNER";

    // No-args constructor
    public AppUser() {}

    // All-args constructor (optional)
    public AppUser(String name, String email, String password, String provider, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.role = role;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProvider() {
        return provider;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
