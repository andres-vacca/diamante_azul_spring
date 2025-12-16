package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Rol;
import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.RolRepository;
import com.example.diamante_azul.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Eliminados: Imports de Cliente y ClienteService

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Eliminado: @Autowired private ClienteService clienteService;

    @GetMapping("/login")
    public String login(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/index";
        }
        return "forms/Login";
    }

    @GetMapping({"/", "/index"})
    public String index(Model model, HttpServletRequest request) {
        // Esta línea es útil para mantener la sesión viva
        request.getSession(true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            String email = auth.getName();
            // Usamos orElse(null) porque findByEmail devuelve Optional en el nuevo repo
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null) {
                model.addAttribute("usuario_nombre", usuario.getNombre());

                if (usuario.getRol() != null) {
                    model.addAttribute("usuario_rol", usuario.getRol().getNombre());
                } else {
                    // Fallback por si acaso
                    model.addAttribute("usuario_rol", "Usuario");
                }
            }
        } else {
            // Valores por defecto para visitantes no logueados
            model.addAttribute("usuario_nombre", "Invitado");
            model.addAttribute("usuario_rol", "Visitante");
        }
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "forms/Register";
    }

    @PostMapping("/perform_register")
    public String registerUser(@ModelAttribute Usuario usuario) {

        // 1. Validar si el EMAIL ya existe
        if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return "redirect:/register?error=emailExists";
        }

        // 2. Validar si el DOCUMENTO ya existe (Importante ahora que está en Usuario)
        // Nota: Asegúrate de tener existsByDocumento o findByDocumento en tu Repo
        // Si no tienes el método 'existsByDocumento', puedes usar el buscador que creamos antes
        if(!usuarioRepository.findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(usuario.getDocumento(), usuario.getDocumento()).isEmpty()) {
            // Si la lista no está vacía al buscar por documento exacto (lógica simplificada)
            // O mejor aún, agrega boolean existsByDocumento(String doc) en tu repo.
            return "redirect:/register?error=documentoExists";
        }

        // 3. Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // 4. Asignar Rol por defecto (CLIENTE)
        // Busca por nombre "CLIENTE" (Asegúrate que en tu BD se llame así, mayúsculas o minúsculas)
        Rol rolCliente = rolRepository.findByNombre("CLIENTE");

        if(rolCliente == null) {
            // Intento de fallback: buscar "Cliente" (primera mayúscula)
            rolCliente = rolRepository.findByNombre("Cliente");
        }

        // Si sigue nulo, asignamos por ID (asumiendo que el ID 2 o 3 es cliente)
        if(rolCliente == null && usuario.getRol() == null) {
            // Esto es solo emergencia. Lo ideal es que tu tabla roles esté bien llena.
            Rol r = new Rol();
            r.setId(2); // Ajusta este ID al ID real de 'CLIENTE' en tu base de datos
            usuario.setRol(r);
        } else {
            usuario.setRol(rolCliente);
        }

        // 5. Datos adicionales
        usuario.setEstadoUsuario("ACTIVO");

        // 6. GUARDAR (Solo guardamos usuario, ¡Y listo!)
        usuarioRepository.save(usuario);

        return "redirect:/login?registrado=true";
    }
}