package com.sergitxin.flexilearn.external;

import org.springframework.stereotype.Component;

@Component
public class AuthExternalFactory {

    public AuthExternalFactory() {}
        
    public AuthExternalPort createAuthAdapter(AuthProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Proveedor de autenticación no válido");
        }

        return switch (provider) {
            case GOOGLE -> new GoogleAuthExternalAdapter();
            case GITHUB -> new GithubAuthExternalAdapter();
        };
    }
}
