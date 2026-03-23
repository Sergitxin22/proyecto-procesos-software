package com.sergitxin.flexilearn.dao;

import com.sergitxin.flexilearn.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByToken(String token);
    Optional<Usuario> findByNombre(String nombre);
    boolean existsByEmail(String email);
}
