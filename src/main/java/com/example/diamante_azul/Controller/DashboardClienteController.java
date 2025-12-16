package com.example.diamante_azul.Controller;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.diamante_azul.Models.Cuotas;
import com.example.diamante_azul.Models.Empeno;
import com.example.diamante_azul.Models.Usuario; // Solo usamos Usuario
// Eliminado Cliente y ClienteRepository

import com.example.diamante_azul.Repository.UsuarioRepository;
import com.example.diamante_azul.Service.CuotasService;
import com.example.diamante_azul.Service.EmpenoService;
import com.example.diamante_azul.Service.ReporteService;

@Controller
@RequestMapping("/dashboard/cliente")
public class DashboardClienteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Eliminado: @Autowired private ClienteRepository clienteRepository;

    @Autowired
    private EmpenoService empenoService;

    @Autowired
    private CuotasService cuotasService;

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String showClienteDashboard(Authentication authentication, Model model) {
        // 1. Verificar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Obtener Usuario logueado (Que ahora ES el Cliente)
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return "redirect:/login";
        }

        // 3. Cargar datos usando el ID del Usuario
        List<Empeno> misEmpenos = new ArrayList<>();
        List<Cuotas> misCuotas = new ArrayList<>();
        long cuotasPendientes = 0;

        try {
            // Nota: Asegúrate de que empenoService.findByClienteId acepte el ID de usuario
            misEmpenos = empenoService.findByClienteId(usuario.getId());

            // Nota: Debes tener un método en cuotasService que busque por 'usuarioCliente.id'
            // Podrías llamarlo findByUsuarioClienteId o mantener findByClienteId si le cambiaste la lógica interna
            misCuotas = cuotasService.findByUsuarioClienteId(usuario.getId());

            // Calcular estadísticas
            cuotasPendientes = misCuotas.stream()
                    .filter(c -> "PENDIENTE".equalsIgnoreCase(c.getEstadoCuota()))
                    .count();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar su información.");
        }

        // 4. Enviar datos a la vista (HTML)
        // Ya no enviamos un objeto "Cliente", enviamos el "Usuario" como cliente
        model.addAttribute("cliente", usuario);

        model.addAttribute("usuario_nombre", usuario.getNombre());
        model.addAttribute("usuario_rol", "Cliente"); // O usuario.getRol().getNombre()
        model.addAttribute("usuario_email", usuario.getEmail());

        model.addAttribute("empenos", misEmpenos);
        model.addAttribute("cuotas", misCuotas);

        model.addAttribute("totalEmpenos", misEmpenos.size());
        model.addAttribute("totalCuotas", misCuotas.size());
        model.addAttribute("cuotasPendientes", cuotasPendientes);

        return "dashboard-cliente";
    }

    // --- REPORTES ---
    // NOTA: Tu ReporteService deberá ser actualizado para recibir 'Usuario' en vez de 'Cliente'

    @GetMapping("/reporte/excel")
    public ResponseEntity<byte[]> descargarExcel(Authentication authentication) throws IOException {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.notFound().build();

        List<Empeno> empenos = empenoService.findByClienteId(usuario.getId());
        List<Cuotas> cuotas = cuotasService.findByUsuarioClienteId(usuario.getId());

        // Aquí asumo que actualizarás ReporteService para aceptar (Usuario, List, List)
        byte[] archivo = reporteService.generarReporteExcel(usuario, empenos, cuotas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_cliente_" + usuario.getDocumento() + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo);
    }

    @GetMapping("/reporte/pdf")
    public ResponseEntity<byte[]> descargarPDF(Authentication authentication) throws IOException {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.notFound().build();

        List<Empeno> empenos = empenoService.findByClienteId(usuario.getId());
        List<Cuotas> cuotas = cuotasService.findByUsuarioClienteId(usuario.getId());

        // Igual aquí, ReporteService debe aceptar Usuario
        byte[] archivo = reporteService.generarReportePDF(usuario, empenos, cuotas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_cliente_" + usuario.getDocumento() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null) return null;
        // findByEmail devuelve Optional en nuestro nuevo repo, usamos orElse(null)
        return usuarioRepository.findByEmail(authentication.getName()).orElse(null);
    }
}