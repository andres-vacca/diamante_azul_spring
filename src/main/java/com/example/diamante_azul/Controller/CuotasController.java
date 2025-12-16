package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Cuotas;
import com.example.diamante_azul.Service.CuotasService;
import com.example.diamante_azul.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/cuotas")
public class CuotasController {

    @Autowired
    private CuotasService cuotasService;



    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/lista")
    public String listarCuotas(Model model) {
        List<Cuotas> cuotas = cuotasService.findAll();
        model.addAttribute("cuotas", cuotas);
        return "cuotas/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("cuota", new Cuotas());

        model.addAttribute("usuarios", usuarioService.findAll());
        return "cuotas/crear";
    }

    @PostMapping("/guardar")
    public String guardarCuota(@ModelAttribute Cuotas cuota, RedirectAttributes redirectAttributes) {
        try {
            if (cuota.getEstadoCuota() == null || cuota.getEstadoCuota().isEmpty()) {
                cuota.setEstadoCuota("PENDIENTE");
            }
            
            cuotasService.save(cuota);
            redirectAttributes.addFlashAttribute("mensaje", "Cuota guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar la cuota: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cuotas/lista";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Cuotas cuota = cuotasService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada con ID: " + id));
        
        model.addAttribute("cuota", cuota);

        model.addAttribute("usuarios", usuarioService.findAll());
        return "cuotas/editar";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCuota(@PathVariable Long id, @ModelAttribute Cuotas cuota, 
                                RedirectAttributes redirectAttributes) {
        try {
            cuota.setIdCuota(id);
            cuotasService.save(cuota);
            redirectAttributes.addFlashAttribute("mensaje", "Cuota actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar la cuota: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cuotas/lista";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCuota(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cuotasService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cuota eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la cuota: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cuotas/lista";
    }

    @PostMapping("/pagar/{id}")
    public String pagarCuota(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cuotas cuota = cuotasService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
            
            cuota.setEstadoCuota("PAGADA");
            cuota.setFechaPago(LocalDate.now());
            cuotasService.save(cuota);
            
            redirectAttributes.addFlashAttribute("mensaje", "Cuota marcada como pagada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al procesar el pago: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/cuotas/lista";
    }

    @GetMapping("/pendientes")
    public String listarCuotasPendientes(Model model) {
        List<Cuotas> cuotasPendientes = cuotasService.findByEstadoCuota("PENDIENTE");
        model.addAttribute("cuotas", cuotasPendientes);
        model.addAttribute("titulo", "Cuotas Pendientes");
        return "cuotas/lista";
    }

    @GetMapping("/vencidas")
    public String listarCuotasVencidas(Model model) {
        List<Cuotas> cuotasVencidas = cuotasService.findCuotasVencidas();
        model.addAttribute("cuotas", cuotasVencidas);
        model.addAttribute("titulo", "Cuotas Vencidas");
        return "cuotas/lista";
    }

    @GetMapping("/seguimiento")
    public String seguimientoCuotas(Model model) {
        model.addAttribute("cuotasPendientes", cuotasService.findByEstadoCuota("PENDIENTE"));
        model.addAttribute("cuotasVencidas", cuotasService.findCuotasVencidas());
        model.addAttribute("cuotasPagadas", cuotasService.findByEstadoCuota("PAGADA"));
        model.addAttribute("totalCuotas", cuotasService.countAll());
        model.addAttribute("totalPendientes", cuotasService.countByEstado("PENDIENTE"));
        model.addAttribute("totalVencidas", cuotasService.countCuotasVencidas());
        model.addAttribute("totalPagadas", cuotasService.countByEstado("PAGADA"));
        return "cuotas/seguimiento";
    }
}