package com.example.diamante_azul.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("")
    public String dashboard(Authentication authentication) {
        // 1. Verificación de seguridad: si no hay usuario, mandar al login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Imprimir información en la consola para depurar
        System.out.println("--- INTENTO DE ACCESO AL DASHBOARD ---");
        System.out.println("Usuario: " + authentication.getName());
        
        // 3. Recorrer los roles del usuario y redirigir
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String rol = authority.getAuthority();
            System.out.println("Rol detectado: '" + rol + "'");

            // Lógica robusta: aceptamos con o sin prefijo "ROLE_" y mayúsculas/minúsculas
            if (rol.equalsIgnoreCase("ROLE_ADMINISTRADOR") || rol.equalsIgnoreCase("ADMINISTRADOR")) {
                System.out.println(">> Redirigiendo a Dashboard ADMIN");
                return "redirect:/dashboard/admin";
            } 
            else if (rol.equalsIgnoreCase("ROLE_EMPLEADO") || rol.equalsIgnoreCase("EMPLEADO")) {
                System.out.println(">> Redirigiendo a Dashboard EMPLEADO");
                return "redirect:/dashboard/empleado";
            } 
            else if (rol.equalsIgnoreCase("ROLE_CLIENTE") || rol.equalsIgnoreCase("CLIENTE")) {
                System.out.println(">> Redirigiendo a Dashboard CLIENTE");
                return "redirect:/dashboard/cliente";
            }
        }

        // 4. Si llegamos aquí, el rol no coincidió con nada esperado
        System.out.println("⚠️ ALERTA: El usuario tiene rol pero no coincide con los esperados. Volviendo a index.");
        return "redirect:/index";
    }
}