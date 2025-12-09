package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/empleado")
public class DashboardEmpleadoController {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmpenoService empenoService;
    
    @Autowired
    private CuotasService cuotasService;
    
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String showEmpleadoDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar autenticación
        Integer usuarioId = (Integer) session.getAttribute("usuario_id");
        String usuarioRol = (String) session.getAttribute("usuario_rol");
        
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión primero");
            return "redirect:/login";
        }
        
        // Verificar que sea empleado o administrador
        if (!"Empleado".equals(usuarioRol) && !"Administrador".equals(usuarioRol)) {
            redirectAttributes.addFlashAttribute("error", "No tiene permisos para acceder a esta sección");
            return "redirect:/login";
        }
        
        // Cargar datos para el dashboard (sin usuarios)
        try {
            // Estadísticas generales
            model.addAttribute("totalClientes", clienteService.listarTodos().size());
            model.addAttribute("totalClientesActivos", clienteService.listarClientesActivos().size());
            model.addAttribute("totalEmpenos", empenoService.listarEmpenosActivos().size());
            model.addAttribute("totalCuotas", cuotasService.findAll().size());
            model.addAttribute("cuotasPendientes", cuotasService.countByEstado("PENDIENTE"));
            model.addAttribute("cuotasVencidas", cuotasService.countCuotasVencidas());
            model.addAttribute("totalProductos", productoService.findAll().size());
            
            // Listas para las tablas (sin usuarios)
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("empenos", empenoService.listarEmpenosActivos());
            model.addAttribute("cuotas", cuotasService.findAll());
            model.addAttribute("productos", productoService.findAll());
            
            // Información del usuario logueado
            model.addAttribute("usuario_nombre", session.getAttribute("usuario_nombre"));
            model.addAttribute("usuario_rol", session.getAttribute("usuario_rol"));
            model.addAttribute("usuario_email", session.getAttribute("usuario_email"));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar los datos: " + e.getMessage());
        }
        
        return "dashboard-empleado";
    }
}