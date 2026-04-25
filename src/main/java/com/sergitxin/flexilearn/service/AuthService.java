package com.sergitxin.flexilearn.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;
import com.sergitxin.flexilearn.external.AuthExternalFactory;
import com.sergitxin.flexilearn.external.AuthExternalPort;
import com.sergitxin.flexilearn.external.AuthProvider;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Service
public class AuthService {
    
    private final UsuarioDao usuarioDao;
    private final AuthExternalFactory authExternalFactory;

    public AuthService(UsuarioDao usuarioDao, AuthExternalFactory authExternalFactory) {
        this.usuarioDao = usuarioDao;
        this.authExternalFactory = authExternalFactory;
    }

    public static String hashPassword(String password, byte[] salt) throws InvalidKeySpecException {
        int iterations = 65536;
        int keyLength = 256;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException ex) {
            System.getLogger(AuthService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public void registrarUsuario(String nombre, String email, String password) {
        if (usuarioDao.existsByEmail(email)) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        byte[] salt = generateSalt();
        String hashedPassword = null;
        try {
            hashedPassword = hashPassword(password, salt);
        } catch (InvalidKeySpecException ex) {
            System.getLogger(AuthService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        nuevoUsuario.setPassword(Base64.getEncoder().encodeToString(salt) + ":" + hashedPassword);
        
        usuarioDao.save(nuevoUsuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDao.findAll();
    }
    
    public Usuario obtenerUsuarioByToken(String token) {
    	Optional<Usuario> usuarioOpt = usuarioDao.findByToken(token);
    			if (usuarioOpt.isPresent()) {
			return usuarioOpt.get();
		} else {
			throw new RuntimeException("Usuario no encontrado para el token proporcionado");
		}
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
            String saltString = usuario.getPassword().split(":")[0];
            byte[] salt = Base64.getDecoder().decode(saltString);
            String hashedPassword = "";
            try {
                hashedPassword = hashPassword(password, salt);
            } catch (InvalidKeySpecException ex) {
                System.getLogger(AuthService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            hashedPassword = saltString + ":" + hashedPassword;
            if (usuario.getPassword().equals(hashedPassword)) {
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

    public void eliminarCuenta(String token) {
        Optional<Usuario> usuarioOpt = usuarioDao.findByToken(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuarioDao.delete(usuario);
        } else {
            throw new RuntimeException("Sesión no encontrada o ya finalizada");
        }
    }
}
