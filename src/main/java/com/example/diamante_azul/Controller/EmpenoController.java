package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Empeno;
import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Service.EmpenoService;
import com.example.diamante_azul.Service.ProductoService;
import com.example.diamante_azul.Service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empenos")
public class EmpenoController {

    private final EmpenoService empenoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public EmpenoController(EmpenoService empenoService, ProductoService productoService,
                            UsuarioService usuarioService) {
        this.empenoService = empenoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    // --- Mapeo para Listar ---
    @GetMapping("/lista")
    public String listarEmpenos(Model model) {
        List<Empeno> empenos = empenoService.listarEmpenosActivos();
        model.addAttribute("empenos", empenos);
        return "empenos/lista";
    }

    // --- Mapeo para Crear (GET) ---
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("empeno", new Empeno());
        model.addAttribute("productosDisponibles", productoService.listarProductosActivos());

        // Usamos el método del servicio que busca por el NOMBRE del rol dentro del objeto Rol
        // Asegúrate de que en la BD los roles se llamen exactamente "CLIENTE" y "EMPLEADO"
        model.addAttribute("clientesDisponibles", usuarioService.listarPorRol("CLIENTE"));
        model.addAttribute("empleadosDisponibles", usuarioService.listarPorRol("EMPLEADO"));

        return "empenos/crear";
    }

    // --- Mapeo para Guardar (POST) ---
    @PostMapping("/guardar")
    public String guardarEmpeno(@ModelAttribute Empeno empeno,
                                @RequestParam("clienteId") Integer clienteId,
                                @RequestParam("usuarioId") Integer usuarioId, // El empleado que atiende
                                @RequestParam("productoId") Integer productoId,
                                RedirectAttributes redirectAttributes) {
        try {
            // 1. Buscar el Cliente
            Usuario cliente = usuarioService.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

            // --- CORRECCIÓN CLAVE AQUÍ ---
            // Antes: cliente.getRol().equals(...) -> Error porque rol es un Objeto
            // Ahora: cliente.getRol().getNombre().equals(...) -> Correcto
            if (cliente.getRol() == null || !"CLIENTE".equalsIgnoreCase(cliente.getRol().getNombre())) {
                throw new RuntimeException("El usuario seleccionado no tiene rol de Cliente válido");
            }
            empeno.setCliente(cliente);

            // 2. Buscar el Empleado
            Usuario empleado = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + usuarioId));

            // Validación opcional para empleado
            // if (!"EMPLEADO".equalsIgnoreCase(empleado.getRol().getNombre()) && !"ADMIN".equalsIgnoreCase(empleado.getRol().getNombre())) {
            //    throw new RuntimeException("El usuario seleccionado no puede registrar empeños");
            // }

            empeno.setUsuario(empleado);

            // 3. Asignar Producto
            empeno.setProducto(productoService.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId)));

            // 4. Guardar
            empenoService.crearEmpeno(empeno);

            redirectAttributes.addFlashAttribute("mensaje", "Empeño registrado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/empenos/lista";

        } catch (Exception e) {
            // Capturamos Exception general para atrapar todo
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear empeño: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/empenos/crear";
        }
    }

    // --- Mapeo para Editar (GET) ---
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        Optional<Empeno> empenoOpt = empenoService.buscarPorId(id);

        if (empenoOpt.isPresent()) {
            model.addAttribute("empeno", empenoOpt.get());
            model.addAttribute("productosDisponibles", productoService.listarProductosActivos());

            // Listas desplegables filtradas por rol
            model.addAttribute("clientesDisponibles", usuarioService.listarPorRol("CLIENTE"));
            model.addAttribute("empleadosDisponibles", usuarioService.listarPorRol("EMPLEADO"));

            return "empenos/editar";
        } else {
            return "redirect:/empenos/lista";
        }
    }

    // --- Mapeo para Actualizar (POST) ---
    @PostMapping("/actualizar/{id}")
    public String actualizarEmpeno(@PathVariable Integer id,
                                   @ModelAttribute Empeno empeno,
                                   @RequestParam("clienteId") Integer clienteId,
                                   @RequestParam("usuarioId") Integer usuarioId,
                                   @RequestParam("productoId") Integer productoId,
                                   RedirectAttributes redirectAttributes) {
        try {
            empeno.setId(id);

            // Asignar Cliente
            Usuario cliente = usuarioService.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            // Validación de seguridad extra (opcional)
            if (!"CLIENTE".equalsIgnoreCase(cliente.getRol().getNombre())) {
                throw new RuntimeException("El ID proporcionado no corresponde a un Cliente");
            }
            empeno.setCliente(cliente);

            // Asignar Empleado
            Usuario empleado = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            empeno.setUsuario(empleado);

            // Asignar Producto
            empeno.setProducto(productoService.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado")));

            empenoService.guardarEmpeno(empeno);

            redirectAttributes.addFlashAttribute("mensaje", "Empeño actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/empenos/lista";
    }

    // --- Mapeo para Finalizar (POST) ---
    @PostMapping("/finalizar/{id}")
    public String finalizarEmpeno(@PathVariable Integer id,
                                  @RequestParam String nuevoEstado,
                                  RedirectAttributes redirectAttributes) {
        try {
            empenoService.finalizarEmpeno(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("mensaje", "Empeño finalizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al finalizar empeño: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/empenos/lista";
    }
}
