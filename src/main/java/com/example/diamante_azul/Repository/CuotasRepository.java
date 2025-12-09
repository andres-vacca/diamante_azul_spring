package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Cuotas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuotasRepository extends JpaRepository<Cuotas, Long> {
    
    // Buscar cuotas por estado
    List<Cuotas> findByEstadoCuota(String estadoCuota);
    
    // Buscar cuotas vencidas
    List<Cuotas> findByFechaVencimientoBeforeAndEstadoCuota(LocalDate fecha, String estadoCuota);
    
    // Buscar cuotas próximas a vencer
    List<Cuotas> findByFechaVencimientoBetweenAndEstadoCuota(LocalDate fechaInicio, LocalDate fechaFin, String estadoCuota);
    
    // Contar cuotas por estado
    Long countByEstadoCuota(String estadoCuota);
    
    // Contar cuotas vencidas
    Long countByFechaVencimientoBeforeAndEstadoCuota(LocalDate fecha, String estadoCuota);
    
    // Buscar cuotas por cliente ID
    @Query("SELECT c FROM Cuotas c WHERE c.cliente.id = :clienteId")
    List<Cuotas> findByClienteId(@Param("clienteId") Integer clienteId);
    
    // Buscar cuotas por factura ID
    @Query("SELECT c FROM Cuotas c WHERE c.factura.id = :facturaId")
    List<Cuotas> findByFacturaId(@Param("facturaId") Long facturaId);
    
    // Buscar cuotas por cliente ID y estado
    @Query("SELECT c FROM Cuotas c WHERE c.cliente.id = :clienteId AND c.estadoCuota = :estado")
    List<Cuotas> findByClienteIdAndEstado(@Param("clienteId") Integer clienteId, @Param("estado") String estado);
}