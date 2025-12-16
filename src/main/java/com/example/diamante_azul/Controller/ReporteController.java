package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Producto;
import com.example.diamante_azul.Service.CuotasService;
import com.example.diamante_azul.Service.EmpenoService;
import com.example.diamante_azul.Service.ProductoService;
import com.example.diamante_azul.Service.ReporteService;
import com.example.diamante_azul.Service.UsuarioService;
import com.example.diamante_azul.Models.Cuotas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importante para la seguridad
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard/reportes")
public class ReporteController {

    @Autowired
    private CuotasService cuotasService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private EmpenoService empenoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ReporteService reporteService; // Servicio que genera los bytes de los archivos

    // =========================================================================
    //              1. REPORTE VENCIDOS (PDF) - RESTRINGIDO A ADMIN
    // =========================================================================

    /**
     * Genera el Reporte de Cuotas Vencidas en formato PDF.
     * Restringido a usuarios con autoridad 'ADMIN'.
     * Ruta: /dashboard/reportes/vencidos
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/vencidos")
    public ResponseEntity<byte[]> generarReporteVencidosPDF() {
        try {
            // 1. Obtener los empeños relacionados con cuotas vencidas
            List<Cuotas> cuotasVencidas = cuotasService.findAll().stream()
                    .filter(c -> "VENCIDA".equalsIgnoreCase(c.getEstadoCuota()))
                    .collect(Collectors.toList());

            // Mapeamos las cuotas a empeños únicos para el PDF de empeños vencidos
            List<com.example.diamante_azul.Models.Empeno> empenosVencidos = cuotasVencidas.stream()
                    .map(Cuotas::getEmpeno)
                    .distinct()
                    .collect(Collectors.toList());

            // 2. Generar el PDF usando el ReporteService
            byte[] pdfBytes = reporteService.generarReporteGlobalPDF(empenosVencidos);

            // 3. Configurar headers para descarga
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "cuotas_vencidas_" + LocalDate.now() + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================================================
    //              2. REPORTE DE INVENTARIO (CSV) - RESTRINGIDO A ADMIN
    // =========================================================================

    /**
     * Genera el Reporte de Inventario de Productos en formato CSV.
     * Restringido a usuarios con autoridad 'ADMIN'.
     * Ruta: /dashboard/reportes/inventario
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/inventario")
    public ResponseEntity<byte[]> generarReporteInventarioCSV() {
        try {
            List<Producto> listaProductos = productoService.findAll();

            // 1. Generar los bytes CSV
            byte[] csvBytes = reporteService.generarReporteInventarioCSV(listaProductos);

            // 2. Configurar headers para CSV
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            String filename = "inventario_productos_" + LocalDate.now() + ".csv";
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================================================
    //              3. REPORTE GENERAL (Excel) - RESTRINGIDO A ADMIN
    // =========================================================================

    /**
     * Genera el Reporte General (Usuarios y Empeños) en formato Excel.
     * Restringido a usuarios con autoridad 'ADMIN'.
     * Ruta: /dashboard/reportes/general
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/general")
    public ResponseEntity<byte[]> generarReporteGeneralExcel() {
        try {
            List<com.example.diamante_azul.Models.Usuario> listaUsuarios = usuarioService.findAll();
            List<com.example.diamante_azul.Models.Empeno> listaEmpenos = empenoService.listarTodosEmpenos();

            // 1. Generar los bytes del Excel
            byte[] excelBytes = reporteService.generarReporteGlobalExcel(listaUsuarios, listaEmpenos);

            // 2. Configurar headers para Excel (.xlsx)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = "reporte_general_" + LocalDate.now() + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}