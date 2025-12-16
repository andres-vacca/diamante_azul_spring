package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Producto;
import com.example.diamante_azul.Service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    // 1. Inyección de dependencia (Mejor práctica con 'final')
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        // Asignación de la dependencia inyectada
        this.productoService = productoService;
    }

    // --- C R U D   O P E R A T I O N S ---

    // 2. LISTAR PRODUCTOS (Solo ACTIVO - Vista por defecto)
    @GetMapping("/lista")
    public String listarProductos(Model model){
        // Llama al método del Service que trae productos activos
        List<Producto> productos = productoService.listarProductosActivos();
        model.addAttribute("productos", productos);
        return "productos/lista";
    }

    // 3. MOSTRAR FORMULARIO DE CREACIÓN (GET)
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model){
        // Añade un objeto vacío para el formulario (usa singular: "producto")
        model.addAttribute("producto", new Producto());
        return "productos/crear";
    }

    // 4. GUARDAR NUEVO PRODUCTO (POST)
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto){
        productoService.guardarProducto(producto);
        // Redirecciona al listado con barra inicial
        return "redirect:/productos/lista";
    }

    // 5. MOSTRAR FORMULARIO DE EDICIÓN (GET)
    @GetMapping("/editar/{id}")
    public String formularioEdicion(@PathVariable Integer id, Model model){
        Producto producto = productoService.findById(id)
                // Manejo de error si el ID no existe
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        // Retorna la vista sin extensión
        return "productos/editar";
    }

    // 6. ACTUALIZAR PRODUCTO (POST)
    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable Integer id, @ModelAttribute Producto producto){
        // Asigna el ID del PathVariable al objeto, asegurando que se actualiza la entidad correcta
        producto.setId(id);
        productoService.guardarProducto(producto);
        return "redirect:/productos/lista";
    }

    // 7. ELIMINAR PRODUCTO (POST para Soft Delete)
    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id){
        // Llama al método de Soft Delete para cambiar el estado a INACTIVO
        productoService.softDeleteById(id);
        return "redirect:/productos/lista";
    }
}