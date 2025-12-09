package com.example.diamante_azul.Repository;

import com.example.diamante_azul.Models.DetalleFacturaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleFacturaVentaRepository extends JpaRepository<DetalleFacturaVenta, Integer> {
}