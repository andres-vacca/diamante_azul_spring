package com.example.diamante_azul.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nombre_usuario", nullable = false, length = 60)
    private String nombre;

    @Column(name = "email_usuario", nullable = false, unique = true, length = 60)
    private String email;

    @Column(name = "contrasena_usuario", nullable = false, length = 60)
    private String contrasena;

    @Column(name = "documento_usuario", nullable = false, length = 20)
    private String documento;

    @Column(name = "tipo_documento_usuario")
    private String tipoDocumento;

    @Column(name = "telefono_usuario", nullable = false, length = 20)
    private String telefono;

    @Column(name = "dirreccion", nullable = false, length = 60)
    private String direccion;

    @Column(name = "estado_usuario")
    private String estadoUsuario; // ACTIVO / INACTIVO

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}