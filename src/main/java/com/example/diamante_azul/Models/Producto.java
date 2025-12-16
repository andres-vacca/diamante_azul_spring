package com.example.diamante_azul.Models;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

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