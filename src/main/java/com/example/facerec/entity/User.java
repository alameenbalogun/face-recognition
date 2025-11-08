package com.example.facerec.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imagePath;

    public User() {}
    public User(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public void setName(String name) { this.name = name; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
