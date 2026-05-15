package com.sergitxin.flexilearn.dto;

import java.util.List;

public class CursoStatsDTO {
    private int totalAlumnos;
    private int totalEjercicios;
    private List<AlumnoProgresoDTO> progresoAlumnos;

    public int getTotalAlumnos() {
        return totalAlumnos;
    }

    public void setTotalAlumnos(int totalAlumnos) {
        this.totalAlumnos = totalAlumnos;
    }

    public int getTotalEjercicios() {
        return totalEjercicios;
    }

    public void setTotalEjercicios(int totalEjercicios) {
        this.totalEjercicios = totalEjercicios;
    }

    public List<AlumnoProgresoDTO> getProgresoAlumnos() {
        return progresoAlumnos;
    }

    public void setProgresoAlumnos(List<AlumnoProgresoDTO> progresoAlumnos) {
        this.progresoAlumnos = progresoAlumnos;
    }
}
