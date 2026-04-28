package com.sergitxin.flexilearn.dto;

public class TestRequestDTO {
    private String codigo;
    private String salidaEsperada;

    public TestRequestDTO() {
        super();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSalidaEsperada() {
        return salidaEsperada;
    }

    public void setSalidaEsperada(String salidaEsperada) {
        this.salidaEsperada = salidaEsperada;
    }
}