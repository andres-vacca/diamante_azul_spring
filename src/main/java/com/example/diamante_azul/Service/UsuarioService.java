package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Models.Rol; // Importante
import com.example.diamante_azul.Repository.UsuarioRepository;
import com.example.diamante_azul.Repository.RolRepository; // Importante
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // Inyectamos esto para buscar el rol

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- MÉTODOS DE BÚSQUEDA ---

    // Este método es el que usa el EmpenoController
    public List<Usuario> listarPorRol(String nombreRol) {
        // Usamos el método modificado del repositorio que busca dentro del objeto Rol
        return usuarioRepository.findByRol_NombreAndEstadoUsuario(nombreRol, "ACTIVO");
    }

    public List<Usuario> findAll() { return usuarioRepository.findAll(); }
    public Optional<Usuario> findById(Integer id) { return usuarioRepository.findById(id); }
    public Usuario findByEmail(String email) { return usuarioRepository.findByEmail(email).orElse(null); }

    // --- EL MÉTODO QUE DABA ERROR ---
    public Usuario registrarUsuario(Usuario usuario) {
        // 1. Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // 2. Asignar Rol por defecto (CLIENTE) si viene nulo
        if (usuario.getRol() == null) {
            // BUSCAMOS EL OBJETO EN LA BD
            Rol rolCliente = rolRepository.findByNombre("CLIENTE");

            // Si no existe en la BD, lo creamos (seguridad)
            if (rolCliente == null) {
                rolCliente = new Rol();
                rolCliente.setNombre("CLIENTE");
                // Asegúrate de setear otros campos obligatorios de Rol si los hay
                rolCliente = rolRepository.save(rolCliente);
            }

            // AHORA SÍ: Pasamos el objeto Rol, no un String
            usuario.setRol(rolCliente);
        }

        // 3. Estado por defecto
        if (usuario.getEstadoUsuario() == null) {
            usuario.setEstadoUsuario("ACTIVO");
        }

        return usuarioRepository.save(usuario);
    }


    // --- MÉTODO PARA ACTUALIZAR USUARIO ---
    public Usuario updateUsuario(Integer id, Usuario usuarioFormulario) {

        // 1. Buscamos el usuario original en la base de datos
        Usuario usuarioDB = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. VALIDACIÓN DE EMAIL
        // Si el email cambió... Y el nuevo email ya existe en la BD... lanzamos error.
        if (!usuarioDB.getEmail().equalsIgnoreCase(usuarioFormulario.getEmail())
                && usuarioRepository.existsByEmail(usuarioFormulario.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso por otro usuario.");
        }

        // 3. VALIDACIÓN DE DOCUMENTO
        // Si el documento cambió... Y el nuevo ya existe... lanzamos error.
        if (!usuarioDB.getDocumento().equals(usuarioFormulario.getDocumento())
                && usuarioRepository.existsByDocumento(usuarioFormulario.getDocumento())) {
            throw new RuntimeException("El documento ya está registrado por otro usuario.");
        }

        // 4. Actualizamos los datos básicos
        usuarioDB.setNombre(usuarioFormulario.getNombre());
        usuarioDB.setTipoDocumento(usuarioFormulario.getTipoDocumento());
        usuarioDB.setDocumento(usuarioFormulario.getDocumento());
        usuarioDB.setEmail(usuarioFormulario.getEmail());
        usuarioDB.setTelefono(usuarioFormulario.getTelefono());
        usuarioDB.setDireccion(usuarioFormulario.getDireccion());
        usuarioDB.setEstadoUsuario(usuarioFormulario.getEstadoUsuario());

        // 5. LÓGICA DE CONTRASEÑA
        // Solo la actualizamos si el campo NO viene vacío.
        // Si viene vacío, asumimos que no quiere cambiar la contraseña.
        if (usuarioFormulario.getContrasena() != null && !usuarioFormulario.getContrasena().isEmpty()) {
            usuarioDB.setContrasena(passwordEncoder.encode(usuarioFormulario.getContrasena()));
        }

        // 6. LÓGICA DE ROL
        // Si cambiaron el rol en el formulario, lo actualizamos.
        // Asegúrate de que el objeto Rol venga cargado o búscalo por ID si es necesario.
        if (usuarioFormulario.getRol() != null) {
            usuarioDB.setRol(usuarioFormulario.getRol());
        }

        // 7. Guardar cambios
        return usuarioRepository.save(usuarioDB);
    }


    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }

    // ... resto de métodos (save, delete, exist, etc)
}