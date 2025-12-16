package com.example.diamante_azul;

import com.example.diamante_azul.Repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class DiamanteAzulApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiamanteAzulApplication.class, args);
    }

    @Bean
    public CommandLineRunner testRolRepository(RolRepository rolRepository) {
        return args -> {
            System.out.println("-------------------------------------");
            System.out.println("--- INICIANDO PRUEBA DE CONEXIÓN DE ROL ---");

            try {
                // Intenta contar los registros
                long count = rolRepository.count();
                System.out.println("✅ Éxito: Número de roles encontrados en la DB: " + count);

                if (count > 0) {
                    // Si encuentra roles, intenta mostrarlos (prueba los getters: getId y getNombre)
                    rolRepository.findAll().forEach(rol ->
                            System.out.println("Rol cargado: " + rol.getNombre() + " (ID: " + rol.getId() + ")")
                    );
                } else {
                    System.out.println("⚠️ Advertencia: El repositorio funciona, pero la tabla 'rol' está vacía o los datos no se leen.");
                }
            } catch (Exception e) {
                System.err.println("❌ ERROR CRÍTICO DE CONEXIÓN/MAPEADO DEL ROL:");
                System.err.println("Mensaje: " + e.getMessage());

                // Muestra la causa raíz si está disponible, lo cual suele ser el error SQL
                if (e.getCause() != null) {
                    System.err.println("Causa principal (Error SQL o JPA): " + e.getCause().getMessage());
                }
            }
            System.out.println("-------------------------------------");
        };
    }
}