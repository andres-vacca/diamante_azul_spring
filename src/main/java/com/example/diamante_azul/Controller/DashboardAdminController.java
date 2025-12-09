package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.UsuarioRepository;
import com.example.diamante_azul.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/admin")
public class DashboardAdminController {

    @Autowired
    private UsuarioRepository usuarioRepository; // Usamos el repositorio directamente para asegurar

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmpenoService empenoService;
    
    @Autowired
    private CuotasService cuotasService;
    
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String showAdminDashboard(Authentication authentication, Model model) {
        // 1. Verificar autenticación con Spring Security (Más seguro y robusto)
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Obtener el usuario real desde la base de datos usando el email del login
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            return "redirect:/login?error=usuarioNoEncontrado";
        }

        // 3. Cargar datos para el dashboard
        try {
            // Estadísticas generales
            model.addAttribute("totalUsuarios", usuarioService.findAll().size());
            model.addAttribute("totalClientes", clienteService.listarTodos().size());
            // Nota: Si alguno de estos métodos no existe en tu servicio, coméntalo temporalmente
            try {
                model.addAttribute("totalClientesActivos", clienteService.listarClientesActivos().size());
                model.addAttribute("totalEmpenos", empenoService.listarEmpenosActivos().size());
                model.addAttribute("cuotasPendientes", cuotasService.countByEstado("PENDIENTE"));
                model.addAttribute("cuotasVencidas", cuotasService.countCuotasVencidas());
            } catch (Exception ex) {
                System.out.println("⚠️ Advertencia: Algún método de conteo no está implementado aún en los servicios.");
            }
            model.addAttribute("totalCuotas", cuotasService.findAll().size());
            
            // Listas para las tablas
            model.addAttribute("usuarios", usuarioService.findAll());
            model.addAttribute("clientes", clienteService.listarTodos());
            // model.addAttribute("empenos", empenoService.listarEmpenosActivos()); // Descomentar si existe
            model.addAttribute("cuotas", cuotasService.findAll());
            model.addAttribute("productos", productoService.findAll());
            
            // 4. Información del usuario para la vista (Usando el objeto real)
            model.addAttribute("usuario_nombre", usuario.getNombre());
            model.addAttribute("usuario_rol", usuario.getRol().getNombre());
            model.addAttribute("usuario_email", usuario.getEmail());
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los datos: " + e.getMessage());
        }
        
        return "dashboard-admin";
    }
}