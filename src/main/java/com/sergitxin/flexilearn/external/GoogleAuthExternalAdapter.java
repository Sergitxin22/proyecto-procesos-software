package com.sergitxin.flexilearn.external;

import org.springframework.stereotype.Component;

/**
 * Ejemplo de adaptador para validar tokens mediante la API de Google.
 */
@Component("googleAuthAdapter")
public class GoogleAuthExternalAdapter implements AuthExternalPort {

    @Override
    public boolean validarTokenExterno(String token) {
        // Aquí iría la lógica real para llamar a Google
        // Por ejemplo, usando RestTemplate o WebClient para hacer un GET a:
        // https://oauth2.googleapis.com/tokeninfo?id_token={token}
        
        System.out.println("Validando token con Google OAuth2 API...");
        
        // Simulación: asumimos que es válido si empieza con cierta estructura
        if (token != null && token.startsWith("google-token-")) {
            return true;
        }
        return false;
    }
}
