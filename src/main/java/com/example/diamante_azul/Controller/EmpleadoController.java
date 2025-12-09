package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Empleado;
import com.example.diamante_azul.Service.EmpleadoService;
import com.example.diamante_azul.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/lista")
    public String listarEmpleados(Model model) {
        List<Empleado> empleados = empleadoService.findAll();
        model.addAttribute("empleados", empleados);
        return "empleados/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("usuarios", usuarioService.findAll());
        return "empleados/crear";
    }

    @PostMapping("/guardar")
    public String guardarEmpleado(@ModelAttribute Empleado empleado, RedirectAttributes redirectAttributes) {
        try {
            if (empleado.getFechaIngreso() == null) {
                empleado.setFechaIngreso(LocalDate.now());
            }
            
            if (empleado.getEstadoEmpleado() == null || empleado.getEstadoEmpleado().isEmpty()) {
                empleado.setEstadoEmpleado("ACTIVO");
            }
            
            empleadoService.save(empleado);
            redirectAttributes.addFlashAttribute("mensaje", "Empleado guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el empleado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/empleados/lista";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model) {
        Empleado empleado = empleadoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
        
        model.addAttribute("empleado", empleado);
        model.addAttribute("usuarios", usuarioService.findAll());
        return "empleados/editar";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarEmpleado(@PathVariable Integer id, @ModelAttribute Empleado empleado, 
                                   RedirectAttributes redirectAttributes) {
        try {
            empleado.setId(id);
            empleadoService.save(empleado);
            redirectAttributes.addFlashAttribute("mensaje", "Empleado actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el empleado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/empleados/lista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Empleado eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el empleado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/empleados/lista";
    }

    @GetMapping("/buscar")
    public String buscarEmpleados(@RequestParam("q") String searchTerm, Model model) {
        List<Empleado> empleados = empleadoService.searchEmpleados(searchTerm);
        model.addAttribute("empleados", empleados);
        model.addAttribute("searchTerm", searchTerm);
        return "empleados/lista";
    }

    @GetMapping("/ver/{id}")
    public String verEmpleado(@PathVariable("id") Integer id, Model model) {
        Empleado empleado = empleadoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
        model.addAttribute("empleado", empleado);
        return "empleados/ver";
    }
}