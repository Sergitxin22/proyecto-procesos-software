package com.sergitxin.flexilearn.dto;

public class DeleteRequestDTO {
    
    private String nombreUsuario;

    public String getNombreUsuario(){
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
