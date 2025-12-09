package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.FacturaVenta;
import com.example.diamante_azul.Models.DetalleFacturaVenta;
import com.example.diamante_azul.Repository.FacturaVentaRepository;
import com.example.diamante_azul.Repository.DetalleFacturaVentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FacturaVentaService {

    private final FacturaVentaRepository facturaVentaRepository;
    private final DetalleFacturaVentaRepository detalleFacturaVentaRepository;

    @Autowired
    public FacturaVentaService(
            FacturaVentaRepository facturaVentaRepository,
            DetalleFacturaVentaRepository detalleFacturaVentaRepository) {
        this.facturaVentaRepository = facturaVentaRepository;
        this.detalleFacturaVentaRepository = detalleFacturaVentaRepository;
    }

    // -------------------------------------------------------------
    // 💾 Lógica para guardar una nueva factura
    // -------------------------------------------------------------

    @Transactional
    public FacturaVenta guardarFactura(FacturaVenta facturaVenta) {

        // 1. Inicializar el total de la factura
        BigDecimal totalFactura = BigDecimal.ZERO;

        // 2. Procesar y calcular los detalles de la factura
        List<DetalleFacturaVenta> detalles = facturaVenta.getDetalles();

        if (detalles != null) {
            for (DetalleFacturaVenta detalle : detalles) {

                // a) Asegurar la referencia bidireccional
                detalle.setFacturaVenta(facturaVenta);

                // b) Calcular el subtotal del detalle
                BigDecimal cantidad = new BigDecimal(detalle.getCantidad());
                BigDecimal precioUnitario = detalle.getPrecioUnitario();

                // *** IMPORTANTE: Usamos .multiply() y .add() de BigDecimal ***
                BigDecimal subtotal = precioUnitario.multiply(cantidad);
                detalle.setSubtotal(subtotal);

                // c) Acumular al total general de la factura
                totalFactura = totalFactura.add(subtotal);
            }
        }

        // 3. Establecer el total final en la cabecera
        facturaVenta.setTotal(totalFactura);

        // 4. Guardar la factura (automáticamente guarda los detalles por CascadeType.ALL)
        return facturaVentaRepository.save(facturaVenta);
    }

    // -------------------------------------------------------------
    // 🔎 Métodos de consulta básicos
    // -------------------------------------------------------------

    public List<FacturaVenta> findAll() {
        return facturaVentaRepository.findAll();
    }

    // CORREGIDO: Cambiado de Long a Integer para coincidir con el Modelo y Repositorio
    public FacturaVenta findById(Integer id) {
        return facturaVentaRepository.findById(id).orElse(null);
    }
}