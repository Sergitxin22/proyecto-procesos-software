package com.sergitxin.flexilearn.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergitxin.flexilearn.entity.Curso;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sergitxin.flexilearn.entity.Curso;

@Repository
public interface CursoDAO extends JpaRepository<Curso, Long> {
	@Query("SELECT SUM(e.puntos) FROM Curso c JOIN c.modulos m JOIN m.ejercicios e WHERE c.id = :cursoId")
	Integer getTotalPuntosByCursoId(@Param("cursoId") Long cursoId);
}
