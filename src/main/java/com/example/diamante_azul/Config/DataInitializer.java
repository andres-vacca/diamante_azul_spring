package com.example.diamante_azul.Config;

import com.example.diamante_azul.Models.Rol;
import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.RolRepository;
import com.example.diamante_azul.Repository.UsuarioRepository;
import com.example.diamante_azul.Service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Crear roles por defecto si no existen
        rolService.crearRolesPorDefecto();

        // 2. Verificar si existe el usuario admin, si no, crearlo
        if (usuarioRepository.findByEmail("admin@diamanteazul.com") == null) {
            crearUsuarioAdmin();
        }
        
        System.out.println("✅ Inicialización de datos completada.");
    }

    private void crearUsuarioAdmin() {
        Usuario admin = new Usuario();
        admin.setNombre("Administrador Principal");
        admin.setEmail("admin@diamanteazul.com");
        // ¡IMPORTANTE! Aquí encriptamos la contraseña
        admin.setContrasena(passwordEncoder.encode("admin123")); 
        
        // Datos requeridos por tu entidad Usuario
        admin.setDocumento("0000000000");
        admin.setTipoDocumento("CC");
        admin.setTelefono("3000000000");
        admin.setDireccion("Oficina Central");
        admin.setEstadoUsuario("ACTIVO");

        // Asignar rol de Administrador
        Rol rolAdmin = rolRepository.findByNombre("Administrador");
        if (rolAdmin != null) {
            admin.setRol(rolAdmin);
            usuarioRepository.save(admin);
            System.out.println("👤 Usuario ADMIN creado: admin@diamanteazul.com / admin123");
        } else {
            System.err.println("⚠️ Error: No se encontró el rol 'Administrador'");
        }
    }
}