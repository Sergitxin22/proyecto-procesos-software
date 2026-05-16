package com.sergitxin.flexilearn.dto;

public class UsuarioActivityStatsDTO {
    private Long id;
    private String nombre;
    private String email;
    private boolean esAdmin;
    private int cursosCreados;
    private int cursosMatriculados;
    private int ejerciciosCompletados;
    private int puntosAcumulados;

    public UsuarioActivityStatsDTO(Long id, String nombre, String email, boolean esAdmin,
            int cursosCreados, int cursosMatriculados, int ejerciciosCompletados, int puntosAcumulados) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.esAdmin = esAdmin;
        this.cursosCreados = cursosCreados;
        this.cursosMatriculados = cursosMatriculados;
        this.ejerciciosCompletados = ejerciciosCompletados;
        this.puntosAcumulados = puntosAcumulados;
    }

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

    public int getCursosCreados() {
        return cursosCreados;
    }

    public void setCursosCreados(int cursosCreados) {
        this.cursosCreados = cursosCreados;
    }

    public int getCursosMatriculados() {
        return cursosMatriculados;
    }

    public void setCursosMatriculados(int cursosMatriculados) {
        this.cursosMatriculados = cursosMatriculados;
    }

    public int getEjerciciosCompletados() {
        return ejerciciosCompletados;
    }

    public void setEjerciciosCompletados(int ejerciciosCompletados) {
        this.ejerciciosCompletados = ejerciciosCompletados;
    }

    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(int puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }
}
