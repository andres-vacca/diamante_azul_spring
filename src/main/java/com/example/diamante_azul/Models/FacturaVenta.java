package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal; // 👈 NUEVO IMPORT NECESARIO

@Entity
@Table(name = "factura_venta")
@Data
public class FacturaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura_venta")
    private Integer id;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta = LocalDate.now();

    // 🛑 CAMBIO CLAVE: Usar BigDecimal para precisión monetaria
    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO; // Inicializar con BigDecimal.ZERO

    // ... (El resto de relaciones y campos se mantienen igual)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_fk", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_fk", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFacturaVenta> detalles;

    public FacturaVenta() {
    }

    public void setDetalles(List<DetalleFacturaVenta> detalles) {
        this.detalles = detalles;
    }

    public List<DetalleFacturaVenta> getDetalles() {
        return detalles;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}