package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Rol;
import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.RolRepository;
import com.example.diamante_azul.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest; // <--- IMPORTANTE: Agregar este import
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

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/index";
        }
        return "forms/Login";
    }

    @GetMapping({"/", "/index"}) 
    public String index(Model model, HttpServletRequest request) { // <--- Agregamos HttpServletRequest
        
        // SOLUCIÓN AL ERROR DE "RESPONSE COMMITTED":
        // Forzamos la creación de la sesión aquí, antes de que se renderice la vista.
        // Así la cookie JSESSIONID se envía en los headers antes de que Thymeleaf
        // empiece a mandar HTML.
        request.getSession(true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email);
            
            if (usuario != null) {
                model.addAttribute("usuario_nombre", usuario.getNombre());
                if (usuario.getRol() != null) {
                    model.addAttribute("usuario_rol", usuario.getRol().getNombre());
                } else {
                    model.addAttribute("usuario_rol", "Cliente");
                }
            } else {
                model.addAttribute("usuario_nombre", "Usuario");
                model.addAttribute("usuario_rol", "Invitado");
            }
        }
        
        return "index";
    }
    
    // ... El resto del archivo (register, perform_register) sigue igual ...
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "forms/Register";
    }

    @PostMapping("/perform_register")
    public String registerUser(@ModelAttribute Usuario usuario) {
        if(usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "redirect:/register?error=emailExists";
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        Rol rolCliente = rolRepository.findByNombre("Cliente");
        if(rolCliente == null) {
             rolCliente = new Rol();
             rolCliente.setId(3);
        }
        usuario.setRol(rolCliente);
        usuario.setEstadoUsuario("ACTIVO");
        usuarioRepository.save(usuario);
        return "redirect:/login?registrado=true";
    }
}