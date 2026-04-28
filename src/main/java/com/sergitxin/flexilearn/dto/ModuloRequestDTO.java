package com.sergitxin.flexilearn.dto;

public class ModuloRequestDTO {
    private String nombre;
    private String descripcion;
    private Long idCurso;

    public ModuloRequestDTO(String nombre, String descripcion, Long idCurso) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.idCurso = idCurso;
	}

    public ModuloRequestDTO() {
		super();
		this.nombre = "";
		this.descripcion = "";
		this.idCurso = 0L;
	}
    
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
