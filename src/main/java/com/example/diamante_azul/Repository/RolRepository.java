package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    Rol findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}