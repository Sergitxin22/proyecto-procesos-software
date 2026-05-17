package com.sergitxin.flexilearn.dto;

import java.util.List;

public class CursoUpdateDTO {
    private String nombre;
    private String categoria;
    private String descripcion;
    private String dificultad;
    private List<ModuloUpdateDTO> modulos;

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

    public List<ModuloUpdateDTO> getModulos() {
        return modulos;
    }

    public void setModulos(List<ModuloUpdateDTO> modulos) {
        this.modulos = modulos;
    }

    // Clase interna para módulos
    public static class ModuloUpdateDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private List<EjercicioUpdateDTO> ejercicios;

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

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public List<EjercicioUpdateDTO> getEjercicios() {
            return ejercicios;
        }

        public void setEjercicios(List<EjercicioUpdateDTO> ejercicios) {
            this.ejercicios = ejercicios;
        }
    }

    // Clase interna para ejercicios
    public static class EjercicioUpdateDTO {
        private Long id;
        private String nombre;
        private String teoria;
        private String enunciado;
        private String codigoInicial;
        private int puntos;
        private String lenguaje;

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
    }
}