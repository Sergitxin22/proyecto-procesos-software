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
/**
 * Servicio para la gestión de la autenticación de usuarios.
 * Maneja el registro, inicio de sesión y gestión de tokens.
 */
public class AuthService {
    
    private final UsuarioDao usuarioDao;
    private final AuthExternalFactory authExternalFactory;

    /**
     * Construye un generador de autenticación base inyectando herramientas propias y externas.
     * @param usuarioDao Herramienta conector de persistencia base de datos.
     * @param authExternalFactory Fabrica proveedora de protocolos externos de autenticación.
     */
    public AuthService(UsuarioDao usuarioDao, AuthExternalFactory authExternalFactory) {
        this.usuarioDao = usuarioDao;
        this.authExternalFactory = authExternalFactory;
    }

    /**
     * Produce el texto plano seguro cifrado utilizando algoritmos estándar con PBKDF2WithHmacSHA256.
     * @param password Clave no cifrada.
     * @param salt Array de byes de aleatorización.
     * @return El hash devuelto y codificado.
     * @throws InvalidKeySpecException en caso de error grave durante el encriptado.
     */
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

    /**
     * Generador matemático de aleatorización de semilla hash para combinarse junto con los passwords.
     * @return Formación SecureRandom array de bytes como salt para cifrar hash.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Gestiona el registro original introduciendo de base sus propiedades al DAO (Database).
     * @param nombre Identificador de texto personal de la cuenta.
     * @param email Correo local de la persona conectada a registrarse.
     * @param password Clave oculta no tratada que se proporcionó.
     */
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

    /**
     * Consulta simple que entrega el registro DAO.
     * @return List de conjunto poblacional de Usuarios.
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDao.findAll();
    }
    
    /**
     * Entrega un conjunto local del Usuario mediante su acceso provisto validando la existencia en BBDD.
     * @param token Encriptado generado por seguridad individualizado.
     * @return Registro o Entidad actual representativa del Usuario.
     */
    public Usuario obtenerUsuarioByToken(String token) {
    	Optional<Usuario> usuarioOpt = usuarioDao.findByToken(token);
    			if (usuarioOpt.isPresent()) {
			return usuarioOpt.get();
		} else {
			throw new RuntimeException("Usuario no encontrado para el token proporcionado");
		}
    }

    /**
     * Valida factores y concede credenciales a través de un logueo base.
     * @param email Correo electrónico principal del login.
     * @param password Intento no hash de contraseña con la que loguear.
     * @return String de Token asimilado (UUID) recién emitido del acceso.
     */
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


    /**
     * Nulo efecto un pase o token (vacía su existencia y termina la duración de sesión).
     * @param token Codigo UUID correspondiente con quien salir del sistema.
     */
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

    /**
     * Purga individual del perfil que invoca esta llamada desvinculando sesión.
     * @param token Pase confirmacional identificativo a liquidar su registro DB.
     */
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
