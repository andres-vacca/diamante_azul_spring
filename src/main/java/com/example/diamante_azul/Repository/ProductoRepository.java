package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // 1. Métodos Derivados Estándar
    // Para buscar productos por nombre (útil para la funcionalidad de búsqueda)
    List<Producto> findByNombre(String nombre);

    // Para filtrar productos por estado (se usa en el Service para findAllActivos())
    List<Producto> findByEstadoProducto(String estado);

    // 2. Método ÚNICO para Modificaciones de Estado (Soft Delete y más)

    /**
     * Realiza una actualización del estado del producto por su ID.
     * Este método se usa para implementar el Soft Delete (cambiando el estado a 'INACTIVO')
     * y para cambiar el estado a 'VENDIDO', 'EN VENTA', etc.
     */
    @Modifying
    @Query("UPDATE Producto p SET p.estadoProducto = :estado WHERE p.id = :id")
    void actualizarEstadoProducto(@Param("id") Integer id, @Param("estado") String estado);
}