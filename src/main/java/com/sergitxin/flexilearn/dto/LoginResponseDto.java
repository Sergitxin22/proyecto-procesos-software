package com.sergitxin.flexilearn.dto;

public class LoginResponseDto {
    private String token;
    private String mensaje;

    public LoginResponseDto(String token, String mensaje) {
        this.token = token;
        this.mensaje = mensaje;
    }

    public LoginResponseDto() {
        this.token = "";
        this.mensaje = "";
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
