package com.example.diamante_azul.Controller;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Service.UsuarioService;
import com.example.diamante_azul.Service.RolService; // <--- ¡Importamos el nuevo servicio!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService; // <--- Inyectamos el Servicio de Rol

    @Autowired
    // Actualizamos el constructor para recibir el RolService
    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // Método auxiliar actualizado para usar el RolService
    private void addRolesToModel(Model model) {
        // Usamos rolService.findAll() en lugar de rolRepository.findAll()
        model.addAttribute("roles", rolService.findAll());
    }

    // 1. LISTAR: GET /usuarios
    @GetMapping("/lista")
    public String listarUsuarios(Model model) {
        // 1. OBTENER DATOS: Llama al servicio para traer todos los usuarios
        List<Usuario> usuarios = usuarioService.findAll(); // Asumiendo que existe findAll()

        // 2. AÑADIR AL MODELO: Pasa la lista a la vista
        model.addAttribute("usuarios", usuarios);

        // 3. DEVOLVER VISTA: Busca el archivo en /templates/usuarios/lista.html
        return "usuarios/lista";
    }

    // 2. CREAR - Mostrar Formulario: GET /usuarios/nuevo
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("usuario", new Usuario());
        addRolesToModel(model);
        return "usuarios/crear";
    }

    // ... (El resto de métodos POST y GET se mantienen igual) ...

    // 4. ACTUALIZAR - Mostrar Formulario: GET /usuarios/editar.html/{id}
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model) {

        // 1. Buscar el usuario en la base de datos
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. Añadir el objeto usuario al modelo para que Thymeleaf pueda usarlo
        model.addAttribute("usuario", usuario);
        addRolesToModel(model);

        // 3. Devolver el nombre de la plantilla HTML (ej: editar_usuario.html)
        return "usuarios/editar";
        // Spring buscará la plantilla en: src/main/resources/templates/usuarios/editar.html
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Usuario usuario,
                             RedirectAttributes redirectAttributes) {
        try {
            usuarioService.updateUsuario(id, usuario); // Llamas al nuevo método
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/usuarios/editar/" + id;
        }
        return "redirect:/usuarios/lista";
    }

    // ...

    // 6. ELIMINAR: GET /usuarios/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) { // Usamos Integer aquí si el Service lo acepta
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios/lista";
    }


    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario){
        usuarioService.registrarUsuario(usuario);
        return "redirect:/usuarios/lista";
    }
}