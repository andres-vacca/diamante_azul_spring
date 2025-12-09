package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Cliente;
import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard/cliente")
public class DashboardClienteController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmpenoService empenoService;
    
    @Autowired
    private CuotasService cuotasService;

    @GetMapping
    public String showClienteDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar autenticación
        Integer usuarioId = (Integer) session.getAttribute("usuario_id");
        String usuarioRol = (String) session.getAttribute("usuario_rol");
        
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión primero");
            return "redirect:/login";
        }
        
        // Verificar que sea cliente
        if (!"Cliente".equals(usuarioRol)) {
            redirectAttributes.addFlashAttribute("error", "No tiene permisos para acceder a esta sección");
            return "redirect:/login";
        }
        
        try {
            // Obtener información del usuario logueado
            Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioId);
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/login";
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Buscar el cliente asociado al usuario (asumiendo que existe una relación)
            // Nota: Esto depende de cómo esté estructurada la relación Usuario-Cliente
            // Si no existe una relación directa, se puede buscar por documento o email
            Cliente cliente = buscarClientePorUsuario(usuario);
            
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontró información de cliente asociada");
                return "redirect:/login";
            }
            
            // Cargar solo los datos del cliente logueado
            model.addAttribute("cliente", cliente);
            
            // Empeños del cliente
            var empenosCliente = empenoService.findByClienteId(cliente.getId());
            model.addAttribute("misEmpenos", empenosCliente);
            model.addAttribute("totalEmpenos", empenosCliente.size());
            
            // Cuotas del cliente
            var cuotasCliente = cuotasService.findByClienteId(cliente.getId());
            model.addAttribute("misCuotas", cuotasCliente);
            model.addAttribute("totalCuotas", cuotasCliente.size());
            
            // Estadísticas del cliente
            long cuotasPendientes = cuotasCliente.stream()
                .filter(c -> "PENDIENTE".equals(c.getEstadoCuota()))
                .count();
            long cuotasPagadas = cuotasCliente.stream()
                .filter(c -> "PAGADA".equals(c.getEstadoCuota()))
                .count();
            
            model.addAttribute("cuotasPendientes", cuotasPendientes);
            model.addAttribute("cuotasPagadas", cuotasPagadas);
            
            // Información del usuario logueado
            model.addAttribute("usuario_nombre", session.getAttribute("usuario_nombre"));
            model.addAttribute("usuario_rol", session.getAttribute("usuario_rol"));
            model.addAttribute("usuario_email", session.getAttribute("usuario_email"));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar los datos: " + e.getMessage());
            return "redirect:/login";
        }
        
        return "dashboard-cliente";
    }
    
    // Método auxiliar para buscar cliente por usuario
    private Cliente buscarClientePorUsuario(Usuario usuario) {
        // Implementar la lógica según la estructura de tu base de datos
        // Opción 1: Si existe una relación directa Usuario -> Cliente
        // return usuario.getCliente();
        
        // Opción 2: Buscar por documento o email
        List<Cliente> clientes = clienteService.listarTodos();
        return clientes.stream()
            .filter(c -> c.getEmail() != null && c.getEmail().equals(usuario.getEmail()))
            .findFirst()
            .orElse(null);
    }
}