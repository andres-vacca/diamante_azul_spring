package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.math.BigDecimal; // Importante


@Getter
@Setter
@Entity
@Table(name = "empeno")
@Data
public class Empeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empeno")
    private Long id; // Usa Long si en DB es bigint(20)

    @Column(name = "estado_empeno", nullable = false, length = 20)
    private String estadoEmpeno;

    @Column(name = "fecha_empeno", nullable = false)
    private LocalDate fechaEmpeno;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    // 💰 CORRECCIÓN: Usamos BigDecimal (decimal(12,2) en DB)
    @Column(name = "interes", precision = 12, scale = 2, nullable = false)
    private BigDecimal interes = BigDecimal.ZERO;

    // 💰 CORRECCIÓN: Usamos BigDecimal (decimal(12,2) en DB)
    @Column(name = "monto_prestado", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoPrestado = BigDecimal.ZERO;

    // 💰 CORRECCIÓN: Usamos BigDecimal (decimal(5,2) en DB)
    @Column(name = "tasa_interes", precision = 5, scale = 2, nullable = false)
    private BigDecimal tasaInteres = BigDecimal.ZERO;


    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_fk", nullable = false)
    private Cliente cliente; // <-- ESTE CAMPO DEBE LLAMARSE 'cliente'



    // Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_fk", nullable = false)
    private Producto producto;

    // Relación con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_fk", nullable = false)
    private Usuario usuario;

    public Empeno() {
    }


    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getMontoPrestado() {
        return montoPrestado;
    }

    public void setMontoPrestado(BigDecimal montoPrestado) {
        this.montoPrestado = montoPrestado;
    }

    public BigDecimal getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(BigDecimal tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public LocalDate getFechaEmpeno() {
        return fechaEmpeno;
    }

    public void setEstadoEmpeno(String estadoEmpeno) {
        this.estadoEmpeno = estadoEmpeno;
    }

    public String getEstadoEmpeno() {
        return estadoEmpeno;
    }

    public void setFechaEmpeno(LocalDate fechaEmpeno) {
        this.fechaEmpeno = fechaEmpeno;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }


    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}