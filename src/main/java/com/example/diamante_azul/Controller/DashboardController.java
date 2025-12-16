package com.example.diamante_azul.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    // Instancia del Logger (estándar profesional para imprimir en consola)
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("")
    public String dashboard(Authentication authentication) {
        // 1. Verificación de seguridad
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Logging informativo (Reemplaza a System.out.println)
        logger.info("--- INTENTO DE ACCESO AL DASHBOARD ---");
        logger.info("Usuario autenticado: {}", authentication.getName());

        // 3. Recorrer los roles del usuario y redirigir
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String rol = authority.getAuthority();
            logger.debug("Rol detectado: '{}'", rol); // debug para no saturar la consola principal

            if (rol.equalsIgnoreCase("ROLE_ADMINISTRADOR") || rol.equalsIgnoreCase("ADMINISTRADOR")) {
                logger.info(">> Redirigiendo a Dashboard ADMIN");
                return "redirect:/dashboard/admin";
            }
            else if (rol.equalsIgnoreCase("ROLE_EMPLEADO") || rol.equalsIgnoreCase("EMPLEADO")) {
                logger.info(">> Redirigiendo a Dashboard EMPLEADO");
                return "redirect:/dashboard/empleado";
            }
            else if (rol.equalsIgnoreCase("ROLE_CLIENTE") || rol.equalsIgnoreCase("CLIENTE")) {
                logger.info(">> Redirigiendo a Dashboard CLIENTE");
                return "redirect:/dashboard/cliente";
            }
        }

        // 4. Si llegamos aquí, alerta de rol desconocido
        logger.warn("⚠️ ALERTA: El usuario {} tiene rol pero no coincide con los esperados. Redirigiendo a index.", authentication.getName());
        return "redirect:/index";
    }
}