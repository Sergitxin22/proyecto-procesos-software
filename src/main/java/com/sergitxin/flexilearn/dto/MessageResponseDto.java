package com.sergitxin.flexilearn.dto;

public class MessageResponseDto {
    private String mensaje;

    public MessageResponseDto(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
