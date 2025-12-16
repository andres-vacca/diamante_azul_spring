package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "cuotas")
public class Cuotas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Long idCuota;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "valor_cuota", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorCuota;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "estado_cuota", length = 20, nullable = false)
    // Ejemplos: "PENDIENTE", "PAGADO", "VENCIDO"
    private String estadoCuota;

    // --- RELACIONES CORREGIDAS ---

    // 1. Relación con el EMPENO (Reemplaza a FacturaVenta)
    // Las cuotas pertenecen a un empeño específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empeno_id", nullable = false)
    private Empeno empeno;

    // 2. Relación con USUARIO (Reemplaza a Cliente)
    // El "cliente" es un Usuario en nuestra nueva estructura
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // Nombre de la columna en la Base de Datos
    private Usuario usuarioCliente;

    public Cuotas() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getIdCuota() { return idCuota; }
    public void setIdCuota(Long idCuota) { this.idCuota = idCuota; }

    public Integer getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(Integer numeroCuota) { this.numeroCuota = numeroCuota; }

    public BigDecimal getValorCuota() { return valorCuota; }
    public void setValorCuota(BigDecimal valorCuota) { this.valorCuota = valorCuota; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getEstadoCuota() { return estadoCuota; }
    public void setEstadoCuota(String estadoCuota) { this.estadoCuota = estadoCuota; }

    // Getter y Setter para EMPENO
    public Empeno getEmpeno() { return empeno; }
    public void setEmpeno(Empeno empeno) { this.empeno = empeno; }

    // Getter y Setter para CLIENTE (Tipo Usuario)
    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public void setUsuarioCliente(Usuario usuarioCliente) {
        this.usuarioCliente = usuarioCliente;
    }

    public Usuario getUsuarioCliente() {
        return usuarioCliente;
    }
}