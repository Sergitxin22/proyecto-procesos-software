package com.sergitxin.flexilearn.entity;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
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
    private boolean esAdmin;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore // NOTA: Añadir esto probablemente nos la lie parda más tarde. Buena suerte!
    private List<Curso> cursosCreados;
    
    @ManyToMany
    @JoinTable(
        name = "matriculas",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    @JsonIgnore
    private List<Curso> cursosMatriculados = new ArrayList<>();
    
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

    public boolean getEsAdmin(){
        return esAdmin;
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

    public void setEsAdmin(boolean esAdmin){
        this.esAdmin = esAdmin;
    }

    public List<Curso> getCursosCreados() {
        return cursosCreados;
    }

    public void setCursosCreados(List<Curso> cursosCreados) {
        this.cursosCreados = cursosCreados;
    }
    
    public List<Curso> getCursosMatriculados() { 
    	return cursosMatriculados; 
    }
    
    public void setCursosMatriculados(List<Curso> cursosMatriculados) { 
    	this.cursosMatriculados = cursosMatriculados; 
    }
}
