package com.sergitxin.flexilearn.dto;

public class ModuloRequestDTO {
    private String nombre;
    private String descripcion;
    private Long idCurso;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdCurso() {
        return idCurso;
    }
}
