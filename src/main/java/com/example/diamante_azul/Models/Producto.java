package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "producto")
@Data

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;

    @Column(name = "nombre_producto", length = 50, nullable = false)
    private String nombre;

    @Column(name = "descripcion_producto", length = 300, nullable = false)
    private String descripcion;

    // ENUM en SQL: ENUM('AGOTADO','DISPONIBLE')
    @Column(name = "estado_producto", nullable = false)
    private String estadoProducto;

    // DECIMAL(10, 2) en SQL
    @Column(name = "precio_producto", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio; // CAMBIADO de Double a BigDecimal


    // ------------------------------------------------------------------
    // RELACIONES ONE-TO-MANY (1:N)
    // Un Producto está asociado a muchos Empeños
    // ------------------------------------------------------------------
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Empeno> empenos;

    // Un Producto está asociado a muchos Detalles de Factura de Venta
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetalleFacturaVenta> detalles;


    // ------------------------------------------------------------------
    // CONSTRUCTOR
    // ------------------------------------------------------------------


    public Producto() {
    }

    // Opcional: Constructor con campos esenciales



    // ------------------------------------------------------------------
    // GETTERS Y SETTERS
    // (Incluyendo los nuevos campos de relación)
    // ------------------------------------------------------------------


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }



    public List<DetalleFacturaVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaVenta> detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estadoProducto;
    }

    public void setEstado(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public List<Empeno> getEmpenos() {
        return empenos;
    }

    public void setEmpenos(List<Empeno> empenos) {
        this.empenos = empenos;
    }
}