package com.sergitxin.flexilearn.dto;

public class EjercicioRequestDTO {
    private String nombre;
    private String teoria;
    private String enunciado;
    private String codigoInicial;
    private int puntos;
    private String lenguaje;
    private Long idModulo;

    public EjercicioRequestDTO(String nombre, String teoria, String enunciado, String codigoInicial, int puntos,
			String lenguaje, Long idModulo) {
		super();
		this.nombre = nombre;
		this.teoria = teoria;
		this.enunciado = enunciado;
		this.codigoInicial = codigoInicial;
		this.puntos = puntos;
		this.lenguaje = lenguaje;
		this.idModulo = idModulo;
	}
    
    public EjercicioRequestDTO() {
		super();
		this.nombre = "";
		this.teoria = "";
		this.enunciado = "";
		this.codigoInicial = "";
		this.puntos = 0;
		this.lenguaje = "";
		this.idModulo = 0L;
	}

	public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTeoria() {
        return teoria;
    }

    public void setTeoria(String teoria) {
        this.teoria = teoria;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Long getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Long idModulo) {
        this.idModulo = idModulo;
    }

}
