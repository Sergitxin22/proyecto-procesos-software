package com.sergitxin.flexilearn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.sergitxin.flexilearn.dao.CursoDAO;
import com.sergitxin.flexilearn.dao.EjercicioDAO;
import com.sergitxin.flexilearn.dao.ModuloDAO;
import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Curso;
import com.sergitxin.flexilearn.entity.Dificultad;
import com.sergitxin.flexilearn.entity.Ejercicio;
import com.sergitxin.flexilearn.entity.Modulo;
import com.sergitxin.flexilearn.entity.Usuario;

@Configuration
public class DataInitializer {

    private final UsuarioDao usuarioDao;
    private final CursoDAO cursoDao;
    private final ModuloDAO moduloDao;
    private final EjercicioDAO ejercicioDao;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(UsuarioDao usuarioDao, CursoDAO cursoDao, ModuloDAO moduloDao, EjercicioDAO ejercicioDao) {
        this.usuarioDao = usuarioDao;
        this.cursoDao = cursoDao;
        this.moduloDao = moduloDao;
        this.ejercicioDao = ejercicioDao;
    }
	
    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            // Comprobamos si la base de datos ya está inicializada
            if (usuarioDao.count() > 0) {            
                logger.info("La base de datos ya contiene usuarios, omitiendo inicialización.");
                return;
            }			
			
            // Creamos algunos usuarios de prueba
            Usuario u1 = new Usuario();
            u1.setNombre("Aitor");
            u1.setEmail("aitor@aitor.com");
            u1.setPassword("N87uqYU4dBVcPw5hyw+tQg==:uDuMYMhqbUJbUZ3vKnWFlSciW0Nnevjss7I6iSgjGPA=");

            Usuario u2 = new Usuario();
            u2.setNombre("Markel");
            u2.setEmail("markel@markel.com");
            u2.setPassword("N87uqYU4dBVcPw5hyw+tQg==:uDuMYMhqbUJbUZ3vKnWFlSciW0Nnevjss7I6iSgjGPA=");

            Usuario u3 = new Usuario();
            u3.setNombre("Aroa");
            u3.setEmail("aroa@aroa.com");
            u3.setEsAdmin(true);
            u3.setPassword("N87uqYU4dBVcPw5hyw+tQg==:uDuMYMhqbUJbUZ3vKnWFlSciW0Nnevjss7I6iSgjGPA=");

            // Guardamos los usuarios
            usuarioDao.saveAll(List.of(u1, u2, u3));
            logger.info("¡Usuarios iniciales guardados correctamente!");

            // Cursos
            Curso c1 = new Curso();
            c1.setNombre("Introducción a Java");
            c1.setCategoria("Programación");
            c1.setDescripcion("Aprende los conceptos básicos de Java.");
            c1.setDificultad(Dificultad.FACIL);
            c1.setUsuario(u3); // Aroa es la creadora

            Curso c2 = new Curso();
            c2.setNombre("Spring Boot Avanzado");
            c2.setCategoria("Programación Backend");
            c2.setDescripcion("Domina el desarrollo con Spring Boot.");
            c2.setDificultad(Dificultad.DIFICIL);
            c2.setUsuario(u1);

            cursoDao.saveAll(List.of(c1, c2));

            // Módulos
            Modulo m1 = new Modulo();
            m1.setNombre("Sintaxis Básica");
            m1.setDescripcion("Variables, tipos de datos y operadores en Java.");
            m1.setCurso(c1);

            Modulo m2 = new Modulo();
            m2.setNombre("POO en Java");
            m2.setDescripcion("Clases, objetos, herencia y polimorfismo.");
            m2.setCurso(c1);

            Modulo m3 = new Modulo();
            m3.setNombre("Rest APIs con Spring Boot");
            m3.setDescripcion("Creación de APIs RESTful usando Controladores y Servicios.");
            m3.setCurso(c2);

            moduloDao.saveAll(List.of(m1, m2, m3));

            // Ejercicios
            Ejercicio e1 = new Ejercicio();
            e1.setNombre("Hola Mundo");
            e1.setTeoria("En Java, el punto de entrada es el método main...");
            e1.setEnunciado("Imprime por consola el texto 'Hola Mundo'.");
            e1.setCodigoInicial("class Main {\n    public static void main(String[] args) {\n        \n    }\n}");
            e1.setPuntos(10);
            e1.setLenguaje("Java");
            e1.setModulo(m1);

            Ejercicio e2 = new Ejercicio();
            e2.setNombre("Calculadora Simple");
            e2.setTeoria("Los operadores aritméticos básicos en Java son...");
            e2.setEnunciado("Crea un método que sume dos números enteros.");
            e2.setCodigoInicial("class Main {\n    public int sumar(int a, int b) {\n        return 0;\n    }\n}");
            e2.setPuntos(20);
            e2.setLenguaje("Java");
            e2.setModulo(m1);

            Ejercicio e3 = new Ejercicio();
            e3.setNombre("Controlador Hola");
            e3.setTeoria("En Spring, la anotación @RestController...");
            e3.setEnunciado("Crea un controlador REST básico que devuelva 'Hola' en la ruta '/'");
            e3.setCodigoInicial("@RestController\npublic class HolaController {\n    \n}");
            e3.setPuntos(50);
            e3.setLenguaje("Java");
            e3.setModulo(m3);

            ejercicioDao.saveAll(List.of(e1, e2, e3));

            logger.info("¡Cursos, Módulos y Ejercicios iniciales guardados correctamente!");
        };
    }
}
