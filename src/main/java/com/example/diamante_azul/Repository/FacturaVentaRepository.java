package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.FacturaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaVentaRepository extends JpaRepository<FacturaVenta, Integer> { // Ojo: Integer si el ID es Integer

    // Métodos válidos basados en tu modelo actual:
    
    // Buscar por el ID del cliente (usando la relación cliente.id)
    List<FacturaVenta> findByCliente_Id(Integer idCliente);

    // Buscar por el ID del usuario/empleado (usando la relación usuario.id)
    List<FacturaVenta> findByUsuario_Id(Integer idUsuario);
    
    // ELIMINADO: List<FacturaVenta> findByEstadoFactura(String estado); 
    // (Este causaba el error porque FacturaVenta no tiene campo 'estadoFactura')
}