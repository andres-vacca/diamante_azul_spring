package com.example.diamante_azul.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // Permitir acceso a recursos estáticos y páginas públicas
        if (requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/") || 
            requestURI.startsWith("/login") || 
            requestURI.startsWith("/api/session/") ||
            requestURI.equals("/") ||
            requestURI.startsWith("/usuarios/nuevo")) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        
        // Verificar si hay sesión activa
        if (session == null || session.getAttribute("usuario_id") == null) {
            response.sendRedirect("/login");
            return false;
        }
        
        // Verificar timeout de sesión
        LocalDateTime loginTime = (LocalDateTime) session.getAttribute("login_time");
        if (loginTime != null) {
            LocalDateTime now = LocalDateTime.now();
            long sessionDurationMinutes = java.time.Duration.between(loginTime, now).toMinutes();
            
            // Si la sesión ha durado más de 8 horas, forzar logout
            if (sessionDurationMinutes > 480) {
                session.invalidate();
                response.sendRedirect("/login?timeout=true");
                return false;
            }
        }
        
        // Verificar permisos por rol para rutas específicas
        String usuarioRol = (String) session.getAttribute("usuario_rol");
        
        if (requestURI.startsWith("/dashboard/admin") && !"Administrador".equals(usuarioRol)) {
            response.sendRedirect("/login?unauthorized=true");
            return false;
        }
        
        if (requestURI.startsWith("/dashboard/empleado") && 
            !"Empleado".equals(usuarioRol) && !"Administrador".equals(usuarioRol)) {
            response.sendRedirect("/login?unauthorized=true");
            return false;
        }
        
        if (requestURI.startsWith("/dashboard/cliente") && !"Cliente".equals(usuarioRol)) {
            response.sendRedirect("/login?unauthorized=true");
            return false;
        }
        
        return true;
    }
}