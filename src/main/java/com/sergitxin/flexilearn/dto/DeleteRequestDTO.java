package com.sergitxin.flexilearn.dto;

public class DeleteRequestDTO {
    
    private String nombreUsuario;

    public DeleteRequestDTO(String nombreUsuario) {
		super();
		this.nombreUsuario = nombreUsuario;
	}
    
    public DeleteRequestDTO() {
		super();
		this.nombreUsuario = "";
	}
    
	public String getNombreUsuario(){
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
