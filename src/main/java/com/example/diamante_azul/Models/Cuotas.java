package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal; // ¡IMPORTANTE: Importar BigDecimal!

@Entity
@Table(name = "cuotas")
public class Cuotas {

    // 1. ATRIBUTOS (CAMPOS) ---------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Long idCuota;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    // CORRECCIÓN CLAVE: Cambiar a BigDecimal para precisión monetaria
    @Column(name = "valor_cuota", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorCuota; // CAMBIADO de Double a BigDecimal

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago; // Puede ser nula, por eso no tiene nullable=false

    @Column(name = "estado_cuota", length = 20, nullable = false)
    private String estadoCuota;


    // 2. CLAVES FORÁNEAS (RELACIONES @ManyToOne) -------------------

    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZACIÓN
    @JoinColumn(name = "id_factura_fk", nullable = false)
    private FacturaVenta factura;

    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZACIÓN
    @JoinColumn(name = "id_cliente_fk", nullable = false)
    private Usuario cliente;


    // 3. CONSTRUCTOR (Requerido por JPA/Hibernate) ----------------
    public Cuotas() {
    }


    // 4. GETTERS Y SETTERS ----------------------------------------

    // ... (idCuota, numeroCuota) ...
    public Long getIdCuota() { return idCuota; }
    public void setIdCuota(Long idCuota) { this.idCuota = idCuota; }
    public Integer getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(Integer numeroCuota) { this.numeroCuota = numeroCuota; }

    // Getters y Setters para valorCuota (Actualizados a BigDecimal)
    public BigDecimal getValorCuota() { // CORREGIDO
        return valorCuota;
    }

    public void setValorCuota(BigDecimal valorCuota) { // CORREGIDO
        this.valorCuota = valorCuota;
    }

    // ... (fechas, estado, relaciones) ...
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getEstadoCuota() { return estadoCuota; }
    public void setEstadoCuota(String estadoCuota) { this.estadoCuota = estadoCuota; }
    public FacturaVenta getFactura() { return factura; }
    public void setFactura(FacturaVenta factura) { this.factura = factura; }
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }
}