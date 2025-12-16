package com.example.diamante_azul.Controller;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // ¡IMPORTANTE!
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.diamante_azul.Models.Cuotas;
import com.example.diamante_azul.Models.Empeno;
import com.example.diamante_azul.Models.Producto;
import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.UsuarioRepository;
import com.example.diamante_azul.Service.CuotasService;
import com.example.diamante_azul.Service.EmpenoService;
import com.example.diamante_azul.Service.ProductoService;
import com.example.diamante_azul.Service.UsuarioService;

@Controller
@RequestMapping("/dashboard/empleado")
public class DashboardEmpleadoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpenoService empenoService;

    @Autowired
    private CuotasService cuotasService;

    @Autowired
    private ProductoService productoService;

    // --- VISTA PRINCIPAL ---
    @GetMapping
    public String showEmpleadoDashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        if (usuario == null) return "redirect:/login?error=usuarioNoEncontrado";

        try {
            // 1. Listas filtradas
            List<Usuario> listaClientes = usuarioService.listarPorRol("CLIENTE");

            // 2. Estadísticas
            model.addAttribute("totalClientes", listaClientes.size());
            model.addAttribute("totalEmpenos", empenoService.listarEmpenosActivos().size());
            model.addAttribute("totalCuotas", cuotasService.findAll().size());
            model.addAttribute("cuotasPendientes", cuotasService.countByEstado("PENDIENTE")); // Asegúrate que este método exista en tu servicio
            model.addAttribute("totalProductos", productoService.findAll().size());

            // 3. Datos para tablas
            model.addAttribute("clientes", listaClientes);
            model.addAttribute("empenos", empenoService.listarEmpenosActivos());
            model.addAttribute("cuotas", cuotasService.findAll());
            model.addAttribute("productos", productoService.findAll());

            // 4. Selects
            model.addAttribute("clientesDisponibles", listaClientes);

            // Header info
            model.addAttribute("usuario_nombre", usuario.getNombre());
            model.addAttribute("usuario_rol", usuario.getRol().getNombre());
            model.addAttribute("usuario_email", usuario.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
        }
        return "dashboard-empleado";
    }

    // =========================================================================
    //                    MÉTODOS DE BÚSQUEDA (AJAX)
    // =========================================================================

    @GetMapping("/buscar/clientes")
    @ResponseBody
    public ResponseEntity<List<Usuario>> buscarClientes(@RequestParam String query) {
        List<Usuario> resultados = usuarioRepository.findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(query, query)
                .stream()
                .filter(u -> "CLIENTE".equalsIgnoreCase(u.getRol().getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/buscar/productos")
    @ResponseBody
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String query) {
        List<Producto> resultados = productoService.findByNombre(query);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/buscar/empenos")
    @ResponseBody
    public ResponseEntity<List<Empeno>> buscarEmpenos(@RequestParam String query) {
        List<Empeno> todos = empenoService.listarEmpenosActivos();
        List<Empeno> filtrados = todos.stream()
                .filter(e -> e.getCliente().getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        e.getCliente().getDocumento().contains(query) ||
                        e.getProducto().getNombre().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtrados);
    }

    // =========================================================================
    //                    GESTIÓN DE CLIENTES
    // =========================================================================

    @GetMapping("/clientes/{id}")
    @ResponseBody
    public ResponseEntity<Usuario> getCliente(@PathVariable Integer id) {
        // CORREGIDO: Manejo de Optional
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/clientes/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCliente(@RequestBody Usuario usuario) {
        try {
            usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(Map.of("message", "Cliente guardado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    //                    GESTIÓN DE EMPEÑOS
    // =========================================================================

    @GetMapping("/empenos/{id}")
    @ResponseBody
    public ResponseEntity<?> getEmpeno(@PathVariable Integer id) {
        return empenoService.buscarPorId(id).map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("cliente", Map.of("id", e.getCliente().getId(), "nombre", e.getCliente().getNombre()));
            map.put("producto", Map.of("id", e.getProducto().getId(), "nombre", e.getProducto().getNombre()));
            map.put("montoPrestado", e.getMontoPrestado());
            map.put("tasaInteres", e.getTasaInteres());
            map.put("fechaEmpeno", e.getFechaEmpeno());
            map.put("fechaVencimiento", e.getFechaVencimiento());
            map.put("estadoEmpeno", e.getEstadoEmpeno());
            return ResponseEntity.ok(map);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/empenos/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarEmpeno(@RequestBody Map<String, Object> data) {
        try {
            Empeno empeno = new Empeno();
            if (data.get("id") != null && !data.get("id").toString().isEmpty()) {
                empeno.setId(Integer.valueOf(data.get("id").toString()));
            }

            Integer clienteId = Integer.valueOf(data.get("clienteId").toString());
            Integer productoId = Integer.valueOf(data.get("productoId").toString());
            Integer usuarioId = Integer.valueOf(data.get("usuarioId").toString()); // Asegúrate de enviar esto desde el front si es necesario, o usa el logueado

            Usuario cliente = usuarioService.findById(clienteId).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            Producto producto = productoService.findById(productoId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // CORREGIDO: Usamos el usuario autenticado si el del mapa falla o es inseguro
            // Pero mantendremos tu lógica si confías en el ID enviado
            Usuario empleado = usuarioService.findById(usuarioId).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            empeno.setCliente(cliente);
            empeno.setProducto(producto);
            empeno.setUsuario(empleado);

            empeno.setMontoPrestado(new BigDecimal(data.get("montoPrestado").toString()));
            empeno.setTasaInteres(new BigDecimal(data.get("tasaInteres").toString()));

            if (data.get("fechaEmpeno") != null) empeno.setFechaEmpeno(LocalDate.parse(data.get("fechaEmpeno").toString()));
            else empeno.setFechaEmpeno(LocalDate.now());

            if (data.get("fechaVencimiento") != null) empeno.setFechaVencimiento(LocalDate.parse(data.get("fechaVencimiento").toString()));
            else empeno.setFechaVencimiento(empeno.getFechaEmpeno().plusDays(30));

            empeno.setEstadoEmpeno(data.get("estadoEmpeno") != null ? data.get("estadoEmpeno").toString() : "ACTIVO");
            empeno.setInteres(BigDecimal.ZERO);

            empenoService.crearEmpeno(empeno);
            return ResponseEntity.ok(Map.of("message", "Empeño guardado"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/empenos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarEmpeno(@PathVariable Integer id) {
        try {
            empenoService.finalizarEmpeno(id, "CANCELADO");
            return ResponseEntity.ok(Map.of("message", "Empeño cancelado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    //                    GESTIÓN DE CUOTAS
    // =========================================================================

    @GetMapping("/cuotas/{id}")
    @ResponseBody
    public ResponseEntity<?> getCuota(@PathVariable Long id) {
        return cuotasService.findById(id).map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idCuota", c.getIdCuota());
            map.put("empeno", Map.of("id", c.getEmpeno().getId()));
            map.put("cliente", Map.of("id", c.getUsuarioCliente().getId())); // Puede fallar si es null
            map.put("numeroCuota", c.getNumeroCuota());
            map.put("valorCuota", c.getValorCuota());
            map.put("fechaVencimiento", c.getFechaVencimiento());
            map.put("fechaPago", c.getFechaPago());
            map.put("estadoCuota", c.getEstadoCuota());
            return ResponseEntity.ok(map);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cuotas/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCuota(@RequestBody Map<String, Object> data) {
        try {
            Cuotas cuota = new Cuotas();

            // 1. Validar ID (Para edición)
            if (data.get("idCuota") != null && !data.get("idCuota").toString().isEmpty()) {
                cuota.setIdCuota(Long.valueOf(data.get("idCuota").toString()));
            }

            // 2. Validar Empeño (OBLIGATORIO)
            if (data.get("empenoId") == null || data.get("empenoId").toString().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Error: Debe seleccionar un Empeño."));
            }
            Integer empenoId = Integer.valueOf(data.get("empenoId").toString());

            Empeno empeno = empenoService.buscarPorId(empenoId)
                    .orElseThrow(() -> new RuntimeException("Empeño no encontrado"));

            // 3. ASIGNACIÓN DE RELACIONES (Aquí estaba el riesgo con tu Entidad)
            cuota.setEmpeno(empeno);

            // ¡IMPORTANTE! Tu entidad tiene DOS campos para el cliente y ambos son 'nullable=false'.
            // Debemos asignar el cliente del empeño a AMBOS para que no falle.
            cuota.setCliente(empeno.getCliente());        // Llena columna 'cliente_id'
            cuota.setUsuarioCliente(empeno.getCliente()); // Llena columna 'usuario_id'

            // 4. Número de Cuota
            if (data.get("numeroCuota") != null && !data.get("numeroCuota").toString().isEmpty()) {
                cuota.setNumeroCuota(Integer.valueOf(data.get("numeroCuota").toString()));
            } else {
                cuota.setNumeroCuota(1);
            }

            // 5. Valor Cuota (OBLIGATORIO - Protección contra NullPointer)
            if (data.get("valorCuota") == null || data.get("valorCuota").toString().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Error: El valor de la cuota es obligatorio."));
            }
            cuota.setValorCuota(new BigDecimal(data.get("valorCuota").toString()));

            // 6. Estado
            if (data.get("estadoCuota") != null) {
                cuota.setEstadoCuota(data.get("estadoCuota").toString());
            } else {
                cuota.setEstadoCuota("PENDIENTE");
            }

            // 7. Fecha Vencimiento
            if (data.get("fechaVencimiento") != null && !data.get("fechaVencimiento").toString().isEmpty()) {
                cuota.setFechaVencimiento(LocalDate.parse(data.get("fechaVencimiento").toString()));
            } else {
                // Si no viene fecha, ponemos 30 días a partir de hoy
                cuota.setFechaVencimiento(LocalDate.now().plusDays(30));
            }

            // 8. Fecha Pago (Protección contra NullPointer)
            if (data.get("fechaPago") != null && !data.get("fechaPago").toString().isEmpty()) {
                cuota.setFechaPago(LocalDate.parse(data.get("fechaPago").toString()));
            } else {
                cuota.setFechaPago(null); // Explícitamente nulo
            }

            cuotasService.save(cuota);
            return ResponseEntity.ok(Map.of("message", "Cuota guardada exitosamente"));

        } catch (Exception e) {
            e.printStackTrace(); // Ver el error real en la consola
            return ResponseEntity.badRequest().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @DeleteMapping("/cuotas/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarCuota(@PathVariable Long id) {
        try {
            cuotasService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Cuota eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    //                    GESTIÓN DE PRODUCTOS
    // =========================================================================

    @GetMapping("/productos/{id}")
    @ResponseBody
    public ResponseEntity<Producto> getProducto(@PathVariable Integer id) {
        return productoService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/productos/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            // El servicio se encarga de todo.
            // Asegúrate que tu Servicio no lance excepción si el ID viene null (eso significa CREAR).
            productoService.guardarProducto(producto);
            return ResponseEntity.ok(Map.of("message", "Producto guardado"));
        } catch (Exception e) {
            e.printStackTrace(); // Ver error en consola
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}