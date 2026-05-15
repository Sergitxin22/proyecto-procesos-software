package com.sergitxin.flexilearn.dto;

public class AlumnoProgresoDTO {
    private Long id;
    private String nombre;
    private String email;
    private int ejerciciosCompletados;
    private int puntosTotales;

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

    public int getEjerciciosCompletados() {
        return ejerciciosCompletados;
    }

    public void setEjerciciosCompletados(int ejerciciosCompletados) {
        this.ejerciciosCompletados = ejerciciosCompletados;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = puntosTotales;
    }
}
