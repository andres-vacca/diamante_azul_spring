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

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscamos por email ya que es el campo único de login
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        // Convertir el rol de la BD a Authority de Spring (ej: "Administrador" -> "ROLE_ADMINISTRADOR")
        String nombreRol = usuario.getRol().getNombre().toUpperCase();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + nombreRol);

        return new User(
                usuario.getEmail(),
                usuario.getContrasena(), // Debe estar encriptada en la DB
                true, true, true, true,
                Collections.singletonList(authority)
        );
    }
}