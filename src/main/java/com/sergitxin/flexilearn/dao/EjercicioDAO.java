package com.sergitxin.flexilearn.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergitxin.flexilearn.entity.Ejercicio;

@Repository
public interface EjercicioDAO extends JpaRepository<Ejercicio, Long> {}
