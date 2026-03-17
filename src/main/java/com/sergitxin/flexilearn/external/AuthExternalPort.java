package com.sergitxin.flexilearn.external;

public interface AuthExternalPort {
    // Puerto para posibles integraciones externas (ej. validación de tokens OAuth, envío de emails, etc.)
    boolean validarTokenExterno(String token);
}
