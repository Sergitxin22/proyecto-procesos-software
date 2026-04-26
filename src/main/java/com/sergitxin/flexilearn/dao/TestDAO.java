package com.sergitxin.flexilearn.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sergitxin.flexilearn.entity.Test;

@Repository
public interface TestDAO extends JpaRepository<Test, Long> {}
