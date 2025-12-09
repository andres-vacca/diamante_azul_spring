package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Empeno; // 🛑 CAMBIO: Empeno
import com.example.diamante_azul.Service.ClienteService;
import com.example.diamante_azul.Service.EmpenoService; // 🛑 CAMBIO: EmpenoService
import com.example.diamante_azul.Service.ProductoService;
import com.example.diamante_azul.Service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empenos") // 🛑 CAMBIO: /empenos
public class EmpenoController { // 🛑 CAMBIO: EmpenoController

    private final EmpenoService empenoService; // 🛑 CAMBIO: empenoService
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public EmpenoController(EmpenoService empenoService, ProductoService productoService,
                            ClienteService clienteService, UsuarioService usuarioService) { // Ajustar el constructor
        this.empenoService = empenoService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    // --- Mapeo para Listar ---
    @GetMapping("/lista")
    public String listarEmpenos(Model model) { // 🛑 CAMBIO: listarEmpenos
        List<Empeno> empenos = empenoService.listarEmpenosActivos(); // 🛑 CAMBIO: listarEmpenosActivos
        model.addAttribute("empenos", empenos); // 🛑 CAMBIO: empenos
        return "empenos/lista"; // Retorna a /src/main/resources/templates/empenos/lista.html
    }

    // --- Mapeo para Crear (GET) ---
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("empeno", new Empeno());

        // Lista de Productos disponibles (asumo que ya existe)
        model.addAttribute("productosDisponibles", productoService.listarProductosActivos());

        // **NUEVO: Incluir Clientes y Usuarios/Empleados para la selección**
        model.addAttribute("clientesDisponibles", clienteService.listarTodos()); // Asegúrate de tener este método en ClienteService
        model.addAttribute("usuariosDisponibles", usuarioService.listarTodosEmpleados()); // Asegúrate de tener este método en UsuarioService

        return "empenos/crear";
    }

    // --- Mapeo para Guardar (POST) ---
    @PostMapping("/guardar")
    public String guardarEmpeno(@ModelAttribute Empeno empeno,
                                // Asumimos que el formulario envía estos campos ocultos o seleccionados:
                                @RequestParam("clienteId") Integer clienteId,
                                @RequestParam("usuarioId") Integer usuarioId) {
        try {
            // 1. OBTENER Y ASIGNAR EL CLIENTE
            // Usamos el servicio de Cliente para obtener el objeto completo
            empeno.setCliente(clienteService.buscarClientePorId(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId)));

            // 2. OBTENER Y ASIGNAR EL USUARIO (el empleado que realiza el empeño)
            // Usamos el servicio de Usuario para obtener el objeto completo
            empeno.setUsuario(usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException(("Usuario no encontrado con Id: " + usuarioId))));

            // 3. Ahora sí, la entidad Empeno está completa y se puede guardar.
            empenoService.crearEmpeno(empeno);

            return "redirect:/empenos/lista";
        } catch (RuntimeException e) {
            System.err.println("Error al crear empeno: " + e.getMessage());
            return "redirect:/empenos/crear?error";
        }
    }

    // --- Mapeo para Editar (GET) ---
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        Optional<Empeno> empenoOpt = empenoService.buscarPorId(id); // 🛑 CAMBIO: Empeno

        if (empenoOpt.isPresent()) {
            model.addAttribute("empeno", empenoOpt.get()); // 🛑 CAMBIO: empeno
            return "empenos/editar"; // Retorna a /src/main/resources/templates/empenos/editar.html
        } else {
            return "redirect:/empenos/lista"; // 🛑 CAMBIO: /empenos/lista
        }
    }

    // --- Mapeo para Actualizar (POST) ---
    @PostMapping("/actualizar/{id}")
    public String actualizarEmpeno(@PathVariable Long id, @ModelAttribute Empeno empeno) {
        // Asumiendo que el campo ID de la entidad es 'id', no 'idEmpeno'
        empeno.setId(id); // 🛑 CORRECCIÓN DE NOMBRE DE SETTER
        empenoService.guardarEmpeno(empeno);
        return "redirect:/empenos/lista";
    }

    // --- Mapeo para Finalizar (POST) ---
    @PostMapping("/finalizar/{id}")
    public String finalizarEmpeno(@PathVariable Integer id, @RequestParam String nuevoEstado) { // 🛑 CAMBIO: finalizarEmpeno
        empenoService.finalizarEmpeno(id, nuevoEstado); // 🛑 CAMBIO: finalizarEmpeno
        return "redirect:/empenos/lista"; // 🛑 CAMBIO: /empenos/lista
    }
}