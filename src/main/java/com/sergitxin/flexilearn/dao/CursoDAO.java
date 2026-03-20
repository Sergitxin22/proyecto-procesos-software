package com.sergitxin.flexilearn.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergitxin.flexilearn.entity.Curso;

@Repository
public interface CursoDAO extends JpaRepository<Curso, Long> {}
