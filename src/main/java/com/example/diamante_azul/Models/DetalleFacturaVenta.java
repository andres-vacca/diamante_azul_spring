package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;

@Entity
@Table(name = "detalle_factura_venta")
@Data
public class DetalleFacturaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_fv") // O el nombre que uses en DB
    private Integer id;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    // Campo calculado o copiado para referencia
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    // ------------------- RELACIÓN MANY-TO-ONE CON FACTURAVENTA -------------------

    // **¡CORRECCIÓN CLAVE!**
    // El nombre del campo 'facturaVenta' coincide con el 'mappedBy' de la FacturaVenta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura_venta_fk", nullable = false)
    private FacturaVenta facturaVenta;

    // ------------------- RELACIÓN MANY-TO-ONE CON PRODUCTO -------------------

    // Un detalle apunta a un solo Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_fk", nullable = false)
    private Producto producto; // Asumo que tienes una entidad Producto

    // Constructor vacío
    public DetalleFacturaVenta() {
    }

    public void setFacturaVenta(FacturaVenta facturaVenta) {
        this.facturaVenta = facturaVenta;
    }

    public FacturaVenta getFacturaVenta() {
        return facturaVenta;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}