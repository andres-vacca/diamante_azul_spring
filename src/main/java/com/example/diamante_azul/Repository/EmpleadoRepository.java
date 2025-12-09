package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    
    Optional<Empleado> findByDocumento(String documento);
    
    Optional<Empleado> findByEmail(String email);
    
    List<Empleado> findByEstadoEmpleado(String estadoEmpleado);
    
    List<Empleado> findByCargo(String cargo);
    
    @Query("SELECT e FROM Empleado e WHERE e.nombre LIKE %?1% OR e.apellido LIKE %?1% OR e.documento LIKE %?1%")
    List<Empleado> findByNombreOrApellidoOrDocumentoContaining(String searchTerm);
    
    @Query("SELECT COUNT(e) FROM Empleado e WHERE e.estadoEmpleado = 'ACTIVO'")
    Long countActiveEmployees();
}