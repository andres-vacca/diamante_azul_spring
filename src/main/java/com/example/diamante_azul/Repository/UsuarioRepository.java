package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Rol;
import com.example.diamante_azul.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // --- Búsquedas Únicas (Para validar Login o Registro) ---

    List<Usuario> findByNombre(String nombre);

    // Buscar por correo (útil para login)
    Optional<Usuario> findByEmail(String email);

    // Buscar por documento (CRÍTICO en una casa de empeño para identificar al cliente)
    Optional<Usuario> findByDocumento(String documento);

    // --- Validaciones (Para evitar duplicados) ---
    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);

    // --- Filtros por Nombre (Buscador general) ---
    // ContainingIgnoreCase permite buscar "Juan" y encontrar "juan perez", "JUAN", etc.
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // --- FILTROS POR ROL (La parte clave de los "nuevos metadatos") ---

    // 1. Buscar usuarios por su Rol (Para llenar los select de 'Cliente' y 'Empleado')
    // Asume que en tu entidad Usuario tienes: private String rol;
    List<Usuario> findByRol(Rol rol);

    // 2. Buscar usuarios por Rol y que estén Activos
    List<Usuario> findByRol_Nombre(String nombreRol);
    // (Muy importante: No quieres que aparezca un empleado despedido en el select para registrar un empeño)
    List<Usuario> findByRol_NombreAndEstadoUsuario(String nombreRol, String estadoUsuario);
    List<Usuario> findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(String nombre, String documento);
    // --- CONSULTAS PERSONALIZADAS (Opcionales pero útiles) ---

    // Buscar si un cliente existe por documento Y es realmente un cliente
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'CLIENTE' AND u.documento = :documento")
    Optional<Usuario> findClienteByDocumento(@Param("documento") String documento);
}