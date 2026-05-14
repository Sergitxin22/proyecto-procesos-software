package com.sergitxin.flexilearn.dto;

public class ForumMessageResponseDTO {
    private String mensaje;
    private String username;
    private String date;

    public ForumMessageResponseDTO(String username, String mensaje, String date) {
		super();
		this.mensaje = mensaje;
        this.username = username;
        this.date = date;
	}
    
    public ForumMessageResponseDTO() {
		super();
		this.mensaje = "";
        this.username = "";
        this.date = "";
	}

	public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
