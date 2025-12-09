package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Método para filtrar solo clientes activos
    List<Cliente> findByEstado(String estado);

    // Búsqueda por identificación (opcional)
    Optional<Cliente> findByIdentificacion(String identificacion);
}