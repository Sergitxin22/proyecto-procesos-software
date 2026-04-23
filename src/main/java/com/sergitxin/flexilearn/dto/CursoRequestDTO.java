package com.sergitxin.flexilearn.dto;

public class CursoRequestDTO {
    private String nombre;
    private String categoria;
    private String descripcion;
    private String dificultad;

    public CursoRequestDTO(String nombre, String categoria, String descripcion, String dificultad) {
		super();
		this.nombre = nombre;
		this.categoria = categoria;
		this.descripcion = descripcion;
		this.dificultad = dificultad;
	}
    
    public CursoRequestDTO() {
		super();
		this.nombre = "";
		this.categoria = "";
		this.descripcion = "";
		this.dificultad = "";
	}

	public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }


}
