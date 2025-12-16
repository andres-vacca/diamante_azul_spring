package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Empeno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpenoRepository extends JpaRepository<Empeno, Integer> {

    // -----------------------------------------------------------------
    //  CASO 1: BÚSQUEDA PARA EL ROL CLIENTE
    // -----------------------------------------------------------------

    // Opción A: Spring Data "Mágico"
    List<Empeno> findByCliente_Id(Integer clienteId);

    // Opción B: Con @Query (Corregido: rol.nombre para comparar texto)
    @Query("SELECT e FROM Empeno e WHERE e.cliente.id = :clienteId AND e.cliente.rol.nombre = 'CLIENTE'")
    List<Empeno> findEmpenosDelCliente(@Param("clienteId") Integer clienteId);

    // Búsqueda por Cliente y Estado
    @Query("SELECT e FROM Empeno e WHERE e.cliente.id = :clienteId AND e.estadoEmpeno = :estado")
    List<Empeno> findByClienteIdAndEstado(@Param("clienteId") Integer clienteId, @Param("estado") String estado);


    // -----------------------------------------------------------------
    //  CASO 2: BÚSQUEDA PARA EL ROL EMPLEADO / ADMIN
    // -----------------------------------------------------------------

    // Opción A: Spring Data "Mágico" (Corregido: campo 'usuario' en vez de 'usuarioRegistra')
    List<Empeno> findByUsuario_Id(Integer usuarioId);

    // Opción B: Con @Query (Corregido: campo 'usuario')
    @Query("SELECT e FROM Empeno e WHERE e.usuario.id = :empleadoId")
    List<Empeno> findPorEmpleadoResponsable(@Param("empleadoId") Integer empleadoId);

    // (Opcional) Corregido campo 'usuario' y 'rol.nombre'
    @Query("SELECT e FROM Empeno e WHERE e.usuario.rol.nombre = 'ADMIN'")
    List<Empeno> findAllCreatedByAdmins();

    // Métodos generales
    List<Empeno> findByEstadoEmpeno(String estado);

    // Método auxiliar útil para los dashboards
    @Query("SELECT e FROM Empeno e WHERE e.estadoEmpeno = 'ACTIVO'")
    List<Empeno> findEmpenosActivos();
}