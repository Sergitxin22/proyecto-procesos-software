package com.sergitxin.flexilearn.service;

import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.external.AuthExternalFactory;
import com.sergitxin.flexilearn.external.AuthExternalPort;
import com.sergitxin.flexilearn.external.AuthProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    
    private final UsuarioDao usuarioDao;
    private final AuthExternalFactory authExternalFactory;

    public AuthService(UsuarioDao usuarioDao, AuthExternalFactory authExternalFactory) {
        this.usuarioDao = usuarioDao;
        this.authExternalFactory = authExternalFactory;
    }

    public void registrarUsuario(String nombre, String email, String password) {
        if (usuarioDao.existsByEmail(email)) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(password); // Recordar hacer hash de password después
        
        usuarioDao.save(nuevoUsuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDao.findAll();
    }

    public String iniciarSesion(String email, String password) {
        // --- PRUEBA DEL FACTORY HARDCODEADO ---
        // Aquí instanciamos de forma dinámica el adaptador de GOOGLE usando el Factory
        AuthExternalPort externalAuth = authExternalFactory.createAuthAdapter(AuthProvider.GOOGLE);
        boolean esValidoExternamente = externalAuth.validarTokenExterno("google-token-test");
        System.out.println("¿Es válido el token en el proveedor externo? " + esValidoExternamente);
        // --------------------------------------

        Optional<Usuario> usuarioOpt = usuarioDao.findByEmail(email);
        
        // En un entorno real se debería comparar un hash de la contraseña usando BCrypt
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getPassword().equals(password)) {
                // Generamos un token (UUID para este ejemplo)
                String token = UUID.randomUUID().toString();
                usuario.setToken(token); // Guardamos la sesión en BBDD
                usuarioDao.save(usuario);
                return token;
            }
        }
        throw new RuntimeException("Credenciales inválidas");
    }

    public void cerrarSesion(String token) {
        Optional<Usuario> usuarioOpt = usuarioDao.findByToken(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setToken(null); // Eliminamos el token de la sesión
            usuarioDao.save(usuario);
        } else {
            throw new RuntimeException("Sesión no encontrada o ya finalizada");
        }
    }
}
