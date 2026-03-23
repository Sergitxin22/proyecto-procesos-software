package com.sergitxin.flexilearn.dto;

public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private boolean esAdmin;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public boolean isEsAdmin() {
        return esAdmin;
    }
    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    
}
