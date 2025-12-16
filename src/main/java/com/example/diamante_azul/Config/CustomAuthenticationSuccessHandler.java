package com.example.diamante_azul.Config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.diamante_azul.Models.Usuario;
import com.example.diamante_azul.Repository.UsuarioRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();

        // 1. CORRECCIÓN: Manejar el Optional correctamente
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // 2. Guardar datos útiles en sesión (Nombre, ID, Email)
        HttpSession session = request.getSession();
        session.setAttribute("usuario_email", email);

        if (usuario != null) {
            session.setAttribute("usuario_nombre", usuario.getNombre());
            session.setAttribute("usuario_id", usuario.getId());
            // Esto es súper útil para que en el controlador no tengas que buscar el ID otra vez
        }

        // 3. Determinar la URL de redirección
        String redirectUrl = determineTargetUrl(authentication);

        // 4. Redirigir
        response.sendRedirect(redirectUrl);
    }

    /**
     * Determina la URL de destino según el rol del usuario
     */
    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            // Asegúrate que tu UserDetailsService esté agregando el prefijo "ROLE_"
            // O usa .contains("ADMINISTRADOR") para ser más flexible
            if (role.equals("ROLE_ADMINISTRADOR") || role.equals("ADMINISTRADOR")) {
                return "/dashboard/admin";
            } else if (role.equals("ROLE_EMPLEADO") || role.equals("EMPLEADO")) {
                return "/dashboard/empleado";
            } else if (role.equals("ROLE_CLIENTE") || role.equals("CLIENTE")) {
                return "/dashboard/cliente";
            }
        }

        return "/index";
    }
}