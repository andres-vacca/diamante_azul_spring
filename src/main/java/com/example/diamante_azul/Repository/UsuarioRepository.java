package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Usuario findByEmail(String email);
    
    List<Usuario> findByNombre(String nombre);
    
    // Devuelve Optional para evitar NullPointerException y manejarlo en el servicio
    Optional<Usuario> findByDocumento(String documento);
    
    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);
    
    // Métodos necesarios para filtros
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByEstadoUsuario(String estado);
    List<Usuario> findByRol_Nombre(String rol);
}