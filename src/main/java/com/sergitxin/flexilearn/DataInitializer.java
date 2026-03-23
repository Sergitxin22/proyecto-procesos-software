package com.sergitxin.flexilearn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.sergitxin.flexilearn.dao.UsuarioDao;
import com.sergitxin.flexilearn.entity.Usuario;

@Configuration
public class DataInitializer {

    private final UsuarioDao usuarioDao;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
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
        };
    }
}
