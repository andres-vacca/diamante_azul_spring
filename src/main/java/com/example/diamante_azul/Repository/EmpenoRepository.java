package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Empeno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmpenoRepository extends JpaRepository<Empeno, Integer> {
    
    // Buscar empeños por estado
    List<Empeno> findByEstadoEmpeno(String estadoEmpeno);
    
    // Buscar empeños por cliente ID
    @Query("SELECT e FROM Empeno e WHERE e.cliente.id = :clienteId")
    List<Empeno> findByClienteId(@Param("clienteId") Integer clienteId);
    
    // Buscar empeños por cliente ID y estado
    @Query("SELECT e FROM Empeno e WHERE e.cliente.id = :clienteId AND e.estadoEmpeno = :estado")
    List<Empeno> findByClienteIdAndEstadoEmpeno(@Param("clienteId") Integer clienteId, @Param("estado") String estado);
    
    // Buscar empeños por usuario (empleado que los registró)
    @Query("SELECT e FROM Empeno e WHERE e.usuario.id = :usuarioId")
    List<Empeno> findByUsuarioId(@Param("usuarioId") Integer usuarioId);
    
    // Buscar empeños vencidos
    @Query("SELECT e FROM Empeno e WHERE e.fechaVencimiento < :fecha AND e.estadoEmpeno = 'ACTIVO'")
    List<Empeno> findEmpenosVencidos(@Param("fecha") LocalDate fecha);
    
    // Buscar empeños próximos a vencer
    @Query("SELECT e FROM Empeno e WHERE e.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND e.estadoEmpeno = 'ACTIVO'")
    List<Empeno> findEmpenosProximosAVencer(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    // Contar empeños por estado
    Long countByEstadoEmpeno(String estadoEmpeno);
}