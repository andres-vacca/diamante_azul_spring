package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Recomendado

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true) // Mejora el rendimiento
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. CORRECCIÓN: Usar .orElseThrow para manejar el Optional
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // 2. Validar que tenga Rol (para evitar NullPointerException)
        String nombreRol = "CLIENTE"; // Rol por defecto si viniera nulo
        if (usuario.getRol() != null) {
            nombreRol = usuario.getRol().getNombre().toUpperCase();
        }

        // 3. Crear la autoridad (ROLE_ADMINISTRADOR, ROLE_CLIENTE, etc.)
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + nombreRol);

        return new User(
                usuario.getEmail(),
                usuario.getContrasena(),
                true, true, true, true,
                Collections.singletonList(authority)
        );
    }
}