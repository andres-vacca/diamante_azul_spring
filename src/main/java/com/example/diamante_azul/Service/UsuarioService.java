package com.example.diamante_azul.Service; // O Controller, depende de la convención que uses. Asumo Service basado en el segundo archivo.

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Models.Rol;
import com.example.diamante_azul.Repository.UsuarioRepository;
import com.example.diamante_azul.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional // Asegura que las operaciones de base de datos se manejen de forma transaccional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    // Inyección de dependencias a través del constructor (práctica recomendada)
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Métodos de CRUD y Búsqueda General ---

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    // Unificado: Usa la firma de Optional<Usuario> para mejor manejo en controladores.
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    // Unificado: Incluye lógica de encriptación de contraseña.
    public Usuario save(Usuario usuario) {
        // Encriptar contraseña solo si es nueva o se ha cambiado (se asume que si tiene valor en un nuevo registro/update es para cambiar)
        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    // --- Métodos de Búsqueda Específicos ---

    /**
     * Devuelve el primer usuario encontrado por nombre (asume que nombre es el campo a usar para nombre de usuario/login)
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return El objeto Usuario o null si no se encuentra.
     */
    public Usuario findByNombreUsuario(String nombreUsuario) {
        // El repositorio original solo tenía findByNombre(String), no findByNombreUsuario
        List<Usuario> usuarios = usuarioRepository.findByNombre(nombreUsuario);
        return (usuarios != null && !usuarios.isEmpty()) ? usuarios.get(0) : null;
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Busca un usuario por su documento de identidad.
     * @param documento El número de documento a buscar.
     * @return El objeto Usuario o null si no se encuentra.
     */
    public Usuario findByDocumento(String documento) {
        // Usa orElse(null) para devolver directamente el objeto o null.
        return usuarioRepository.findByDocumento(documento).orElse(null);
    }

    /**
     * Busca todos los usuarios que tienen un Rol con el nombre especificado.
     * @param nombreRol El nombre del rol.
     * @return Una lista de Usuarios.
     */
    public List<Usuario> findByRolNombre(String nombreRol) {
        return usuarioRepository.findByRol_Nombre(nombreRol);
    }


    // --- Métodos de Lógica de Negocio y Utilidad ---

    /**
     * Método específico para registrar un nuevo usuario (típicamente clientes) con lógica de roles y estado por defecto.
     * @param usuario El objeto Usuario a registrar.
     * @return El Usuario guardado con la contraseña cifrada y el rol/estado asignados.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Cifrar la contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // Asignar Rol 'Cliente' si no tiene uno
        if (usuario.getRol() == null) {
            Rol rolCliente = rolRepository.findByNombre("Cliente");
            if (rolCliente == null) {
                // Crear y guardar el rol cliente si no existe (fallback)
                rolCliente = new Rol();
                rolCliente.setNombre("Cliente");
                rolCliente = rolRepository.save(rolCliente);
            }
            usuario.setRol(rolCliente);
        }

        // Asignar Estado 'ACTIVO' si no tiene uno
        if (usuario.getEstadoUsuario() == null) {
            usuario.setEstadoUsuario("ACTIVO");
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Lista todos los usuarios con el rol "Empleado".
     * @return Una lista de Usuarios Empleados o una lista vacía.
     */
    public List<Usuario> listarTodosEmpleados() {
        Rol rolEmpleado = rolRepository.findByNombre("Empleado");
        if (rolEmpleado != null) {
            // Asumiendo que existe un findByRol(Rol rol) en UsuarioRepository
            return usuarioRepository.findByRol(rolEmpleado);
        }
        return List.of();
    }

    // Métodos de Existencia

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existsByDocumento(String documento) {
        return usuarioRepository.existsByDocumento(documento);
    }
}