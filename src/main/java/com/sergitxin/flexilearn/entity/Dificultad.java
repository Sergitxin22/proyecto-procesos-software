package com.sergitxin.flexilearn.entity;

public enum Dificultad {
    FACIL, MEDIO, DIFICIL;

    public static Dificultad stringToDificultad(String dificultad) {
        switch (dificultad) {
            case "facil" -> {
                return FACIL;
            }
            case "medio" -> {
                return MEDIO;
            }
            case "dificil" -> {
                return DIFICIL;
            }
            default -> throw new AssertionError();
        }
    }
}