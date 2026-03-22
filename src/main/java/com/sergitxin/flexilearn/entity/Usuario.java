package com.sergitxin.flexilearn.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String password;
    
    private String nombre;
    private String token; // Para guardar la sesión en BBDD

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore // NOTA: Añadir esto probablemente nos la lie parda más tarde. Buena suerte!
    private List<Curso> cursosCreados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Curso> getCursosCreados() {
        return cursosCreados;
    }

    public void setCursosCreados(List<Curso> cursosCreados) {
        this.cursosCreados = cursosCreados;
    }
}
