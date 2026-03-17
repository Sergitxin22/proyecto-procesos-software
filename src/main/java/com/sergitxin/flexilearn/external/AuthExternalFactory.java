package com.sergitxin.flexilearn.external;

import org.springframework.stereotype.Component;

@Component
public class AuthExternalFactory {

    public AuthExternalFactory() {}
        
    public AuthExternalPort createAuthAdapter(AuthProvider provider) {
        switch (provider) {
            case GOOGLE:
                return new GoogleAuthExternalAdapter();
            case GITHUB:
                return new GithubAuthExternalAdapter();
            default:
                throw new IllegalArgumentException("Proveedor de autenticación no válido: " + provider);
        }
    }
}
