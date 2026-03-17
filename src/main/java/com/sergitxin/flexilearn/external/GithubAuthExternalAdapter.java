package com.sergitxin.flexilearn.external;

import org.springframework.stereotype.Component;

/**
 * Ejemplo de adaptador para validar tokens mediante la API de GitHub.
 */
@Component("githubAuthAdapter")
public class GithubAuthExternalAdapter implements AuthExternalPort {

    @Override
    public boolean validarTokenExterno(String token) {
        // Aquí iría la lógica real para llamar a GitHub
        // Por ejemplo enviando un GET a https://api.github.com/user
        // con el header "Authorization: Bearer " + token
        
        System.out.println("Validando token con GitHub API...");
        
        // Simulación: los tokens de GitHub suelen empezar con prefijos como "ghp_" o "gho_"
        if (token != null && (token.startsWith("gho_") || token.startsWith("ghp_"))) {
            return true;
        }
        return false;
    }
}
