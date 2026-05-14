package com.sergitxin.flexilearn.dto;

public class MessageDTO {
    private String mensaje;

    public MessageDTO(String mensaje) {
		super();
		this.mensaje = mensaje;
	}
    
    public MessageDTO() {
		super();
		this.mensaje = "";
	}

	public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
