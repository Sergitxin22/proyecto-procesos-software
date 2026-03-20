package com.sergitxin.flexilearn.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergitxin.flexilearn.entity.Modulo;

@Repository
public interface ModuloDAO extends JpaRepository<Modulo, Long> {}
