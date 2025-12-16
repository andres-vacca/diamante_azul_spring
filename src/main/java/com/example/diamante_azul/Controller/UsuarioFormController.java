package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard/usuarios")
public class UsuarioFormController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. MOSTRAR FORMULARIO NUEVO
    @GetMapping("/nuevo")
    public String formNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-form";
    }

    // 2. MOSTRAR FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String formEditarUsuario(@PathVariable Integer id, Model model, RedirectAttributes redirectAttrs) {
        // Tu servicio devuelve Optional, así que usamos .orElse(null)
        Usuario usuario = usuarioService.findById(id).orElse(null);

        if (usuario == null) {
            redirectAttrs.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/dashboard/admin";
        }

        model.addAttribute("usuario", usuario);
        return "usuario-form";
    }

    // 3. PROCESAR GUARDADO (El cambio importante está aquí)
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam(value = "claveSinEncriptar", required = false) String claveSinEncriptar,
                                 Authentication auth,
                                 RedirectAttributes redirectAttrs) {
        try {
            // PASO A: Asignar la contraseña "en crudo" al objeto
            // Tu servicio decidirá si la encripta (si es nuevo) o si la ignora (si está vacía en update)
            usuario.setContrasena(claveSinEncriptar);

            // PASO B: Decidir si es CREAR o ACTUALIZAR
            if (usuario.getId() == null) {
                // --- NUEVO USUARIO ---
                // Tu método registrarUsuario ya encripta y pone roles por defecto
                usuarioService.registrarUsuario(usuario);
            } else {
                // --- ACTUALIZAR USUARIO ---
                // Tu método updateUsuario ya valida email, documento y encripta si hace falta
                usuarioService.updateUsuario(usuario.getId(), usuario);
            }

            // PASO C: Éxito
            redirectAttrs.addFlashAttribute("mensaje", "Usuario guardado exitosamente.");

            // PASO D: Redirección según rol logueado
            if (auth != null) {
                String rolLogueado = auth.getAuthorities().toString();
                if (rolLogueado.contains("ADMIN")) {
                    return "redirect:/dashboard/admin";
                } else if (rolLogueado.contains("EMPLEADO")) {
                    return "redirect:/dashboard/empleado";
                }
            }
            return "redirect:/login";

        } catch (Exception e) {
            // Si tu servicio lanza RuntimeException (ej: "Email repetido"), cae aquí
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());

            // Importante: Si falla, intentamos volver al formulario correcto
            if (usuario.getId() != null) {
                return "redirect:/dashboard/usuarios/editar/" + usuario.getId();
            } else {
                return "redirect:/dashboard/usuarios/nuevo";
            }
        }
    }
}