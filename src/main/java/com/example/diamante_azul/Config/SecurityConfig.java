package com.example.diamante_azul.Config;

import com.example.diamante_azul.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Recursos Públicos (Login, Registro, CSS, JS, Imágenes)
                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/register",
                                "/perform_register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 2. Rutas por Rol (Coinciden con ROLE_... del UserDetailsService)
                        .requestMatchers("/dashboard/admin/**").hasAnyRole("ADMINISTRADOR", "ADMIN")
                        .requestMatchers("/dashboard/empleado/**").hasAnyRole("EMPLEADO", "ADMINISTRADOR")
                        .requestMatchers("/dashboard/cliente/**").hasAnyRole("CLIENTE") // El cliente solo entra a lo suyo

                        // 3. Cualquier otra cosa requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login") // Debe coincidir con th:action en Login.html
                        .usernameParameter("email")           // <--- ¡IMPORTANTE! Coincide con name="email" del HTML
                        .passwordParameter("password")
                        .successHandler(successHandler)       // Tu manejador personalizado para redirigir
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );

        return http.build();
    }

    // Bean para encriptar contraseñas (Usado en el Registro y Login)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración del proveedor de autenticación (Conecta tu UserDetailsService)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}