package com.example.diamante_azul.Controller;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.diamante_azul.Service.RolService;

@Controller
@RequestMapping("/dashboard/admin")
public class DashboardAdminController {

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

    @Autowired
    private RolService rolService; // Inyección de RolService para cargar roles

    // --- VISTA PRINCIPAL DEL DASHBOARD ---
    @GetMapping
    public String showAdminDashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return "redirect:/login?error=usuarioNoEncontrado";
        }

        try {
            // 0. Cargar roles para el selector de Usuario (CORRECCIÓN ROL)
            model.addAttribute("roles", rolService.findAll());

            // 1. Listas filtradas por ROL
            List<Usuario> listaClientes = usuarioService.listarPorRol("CLIENTE");
            List<Usuario> listaEmpleados = usuarioService.listarPorRol("EMPLEADO");
            List<Usuario> todosUsuarios = usuarioService.findAll();

            // 2. Estadísticas (KPIs)
            model.addAttribute("totalUsuarios", todosUsuarios.size());
            model.addAttribute("totalClientes", listaClientes.size());
            model.addAttribute("totalEmpenos", empenoService.listarEmpenosActivos().size());

            model.addAttribute("cuotasPendientes", cuotasService.countByEstado("PENDIENTE"));
            model.addAttribute("cuotasVencidas", cuotasService.countCuotasVencidas());
            model.addAttribute("totalCuotas", cuotasService.findAll().size());

            // 3. Cargar Productos y Filtrar los DISPONIBLES (CORRECCIÓN PRODUCTO)
            List<Producto> todosProductos = productoService.findAll();

            // Filtra solo los productos DISPONIBLES para el modal de Empeño
            List<Producto> productosDisponibles = todosProductos.stream()
                    .filter(p -> "DISPONIBLE".equalsIgnoreCase(p.getEstado()))
                    .collect(Collectors.toList());

            // 4. Datos para llenar las Tablas HTML
            model.addAttribute("usuarios", todosUsuarios);
            model.addAttribute("clientes", listaClientes);
            model.addAttribute("empenos", empenoService.listarEmpenosActivos());
            model.addAttribute("cuotas", cuotasService.findAll());
            model.addAttribute("productos", todosProductos); // Todos los productos para la tabla general

            // 5. Listas para los Selects de los Modales (Crear/Editar)
            model.addAttribute("clientesDisponibles", listaClientes);
            model.addAttribute("empleadosDisponibles", listaEmpleados);
            model.addAttribute("productosDisponibles", productosDisponibles); // <-- ¡LA LISTA FILTRADA!

            // 6. Info del Usuario Logueado (Header)
            model.addAttribute("usuario_nombre", usuario.getNombre());
            model.addAttribute("usuario_rol", usuario.getRol().getNombre());
            model.addAttribute("usuario_email", usuario.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los datos: " + e.getMessage());
        }

        return "dashboard-admin";
    }

    // =========================================================================
    //                    MÉTODOS DE BÚSQUEDA (AJAX)
    // =========================================================================

    @GetMapping("/buscar/usuarios")
    @ResponseBody
    public ResponseEntity<List<Usuario>> buscarUsuarios(@RequestParam String query) {
        List<Usuario> resultados = usuarioRepository.findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(query, query);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/buscar/clientes")
    @ResponseBody
    public ResponseEntity<List<Usuario>> buscarClientes(@RequestParam String query) {
        List<Usuario> resultados = usuarioRepository.findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(query, query)
                .stream()
                .filter(u -> u.getRol() != null && "CLIENTE".equalsIgnoreCase(u.getRol().getNombre()))
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
    //                    MÉTODOS DE GUARDADO (POST)
    // =========================================================================

    // --- GUARDAR EMPEÑO ---
    @PostMapping("/empenos/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarEmpeno(@RequestBody Map<String, Object> data) {
        try {
            Empeno empeno = new Empeno();

            if (data.get("id") != null && !data.get("id").toString().isEmpty()) {
                empeno.setId(Integer.valueOf(data.get("id").toString()));
            }

            if (data.get("clienteId") == null || data.get("productoId") == null || data.get("usuarioId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Faltan datos requeridos (Cliente, Producto o Usuario)"));
            }

            Integer clienteId = Integer.valueOf(data.get("clienteId").toString());
            Integer productoId = Integer.valueOf(data.get("productoId").toString());
            Integer usuarioId = Integer.valueOf(data.get("usuarioId").toString());

            // 1. Asignar CLIENTE
            Usuario cliente = usuarioService.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            if(cliente.getRol() == null || !"CLIENTE".equalsIgnoreCase(cliente.getRol().getNombre())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El usuario seleccionado no tiene rol de Cliente"));
            }
            empeno.setCliente(cliente);

            // 2. Asignar PRODUCTO
            Producto producto = productoService.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            empeno.setProducto(producto);

            // 3. Asignar EMPLEADO (Usuario que registra)
            Usuario empleado = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            empeno.setUsuario(empleado);

            // 4. Datos financieros
            String montoStr = data.get("montoPrestado") != null ? data.get("montoPrestado").toString() : "0";
            String tasaStr = data.get("tasaInteres") != null ? data.get("tasaInteres").toString() : "0";

            empeno.setMontoPrestado(new BigDecimal(montoStr));
            empeno.setTasaInteres(new BigDecimal(tasaStr));

            // 5. Fechas
            if (data.get("fechaEmpeno") != null) empeno.setFechaEmpeno(LocalDate.parse(data.get("fechaEmpeno").toString()));
            else empeno.setFechaEmpeno(LocalDate.now());

            if (data.get("fechaVencimiento") != null) empeno.setFechaVencimiento(LocalDate.parse(data.get("fechaVencimiento").toString()));
            else empeno.setFechaVencimiento(empeno.getFechaEmpeno().plusDays(30));

            empeno.setEstadoEmpeno("ACTIVO");
            empeno.setInteres(BigDecimal.ZERO);

            empenoService.crearEmpeno(empeno);

            return ResponseEntity.ok(Map.of("message", "Empeño registrado exitosamente"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error al procesar empeño: " + e.getMessage()));
        }
    }

    // --- GUARDAR CUOTA ---
    @PostMapping("/cuotas/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCuota(@RequestBody Map<String, Object> data) {
        try {
            Cuotas cuota = new Cuotas();

            if (data.get("idCuota") != null && !data.get("idCuota").toString().isEmpty()) {
                cuota.setIdCuota(Long.valueOf(data.get("idCuota").toString()));
            }

            if (data.get("empenoId") == null || data.get("empenoId").toString().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Error: El ID del Empeño es obligatorio."));
            }
            Integer empenoId = Integer.valueOf(data.get("empenoId").toString());

            Empeno empeno = empenoService.buscarPorId(empenoId)
                    .orElseThrow(() -> new RuntimeException("Empeño no encontrado"));

            cuota.setEmpeno(empeno);
            cuota.setCliente(empeno.getCliente());
            cuota.setUsuarioCliente(empeno.getCliente());

            if (data.get("numeroCuota") != null && !data.get("numeroCuota").toString().isEmpty()) {
                cuota.setNumeroCuota(Integer.valueOf(data.get("numeroCuota").toString()));
            } else {
                cuota.setNumeroCuota(1);
            }

            if (data.get("valorCuota") == null || data.get("valorCuota").toString().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Error: El valor de la cuota es obligatorio."));
            }
            cuota.setValorCuota(new BigDecimal(data.get("valorCuota").toString()));

            if (data.get("estadoCuota") != null) {
                cuota.setEstadoCuota(data.get("estadoCuota").toString());
            } else {
                cuota.setEstadoCuota("PENDIENTE");
            }

            if (data.get("fechaVencimiento") != null && !data.get("fechaVencimiento").toString().isEmpty()) {
                cuota.setFechaVencimiento(LocalDate.parse(data.get("fechaVencimiento").toString()));
            } else {
                cuota.setFechaVencimiento(LocalDate.now().plusDays(30));
            }

            if (data.get("fechaPago") != null && !data.get("fechaPago").toString().isEmpty()) {
                cuota.setFechaPago(LocalDate.parse(data.get("fechaPago").toString()));
            } else {
                cuota.setFechaPago(null);
            }

            cuotasService.save(cuota);
            return ResponseEntity.ok(Map.of("message", "Cuota guardada exitosamente"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error interno al guardar cuota: " + e.getMessage()));
        }
    }

    // --- GUARDAR PRODUCTO ---
    @PostMapping("/productos/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            productoService.guardarProducto(producto);
            return ResponseEntity.ok(Map.of("message", "Producto guardado"));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    // --- GUARDAR USUARIO ---
    @PostMapping("/usuarios/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarUsuario(@RequestBody Usuario usuario) {
        try {
            usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(Map.of("message", "Usuario guardado"));
        } catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    // =========================================================================
    //                    MÉTODOS DE ELIMINACIÓN
    // =========================================================================

    @DeleteMapping("/empenos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarEmpeno(@PathVariable Integer id) {
        try {
            empenoService.finalizarEmpeno(id, "CANCELADO");
            return ResponseEntity.ok(Map.of("message", "Empeño cancelado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}