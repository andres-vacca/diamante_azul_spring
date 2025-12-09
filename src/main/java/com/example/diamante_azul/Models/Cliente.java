package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

// NOTA: Asumo que estás usando Lombok para simplificar el código.
// Si no usas Lombok, debes añadir manualmente los Getters y Setters.
import lombok.Data;


@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    // 1. ATRIBUTOS (CAMPOS) ---------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "apellido", length = 100, nullable = false)
    private String apellido;

    @Column(name = "identificacion", length = 20, unique = true, nullable = false)
    private String identificacion; // Cédula, DNI, etc.

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "direccion", length = 255)
    private String direccion;



    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro = LocalDate.now();

    @Column(name = "estado", length = 10, nullable = false)
    private String estado = "ACTIVO";


    // 2. RELACIONES @OneToMany -------------------------------------

    // Relación con Empeño: Un Cliente puede tener muchos Empeños
    // MappedBy="cliente" indica que la FK está en la tabla 'empeno'
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empeno> empenos;

    // Relación con FacturaVenta: Un Cliente puede tener muchas Facturas de Venta
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVenta> facturasVenta;


    // 3. CONSTRUCTOR (Si no usas @Data, necesitarías un constructor vacío)
    public Cliente() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}