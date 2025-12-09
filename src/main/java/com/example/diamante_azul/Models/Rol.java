package com.example.diamante_azul.Models;

// En com.example.diamante_azul.Models.Rol.java

import jakarta.persistence.*;
import lombok.Data; // O similar

@Data
@Entity
@Table(name = "rol") // <-- El nombre de la tabla es 'rol'
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ELIMINA CUALQUIER ANOTACIÓN @Column(name="id_rol")
    private Integer id; // <-- Se mapea por defecto a la columna 'id'

    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    // ... otros métodos, constructores ...
}