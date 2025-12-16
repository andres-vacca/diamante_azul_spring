/**
 * Dashboard Authentication System (Optimizado para Spring Boot)
 * * Este script ya no realiza peticiones en bucle al servidor.
 * En su lugar, confía en el estado que Thymeleaf renderizó en el HTML.
 */

document.addEventListener('DOMContentLoaded', function() {
    initializeDashboardAuth();
});

function initializeDashboardAuth() {
    // 1. Obtener estado de autenticación directamente del HTML
    // (Renderizado por Thymeleaf en: <body data-authenticated="...">)
    const isAuthenticated = document.body.dataset.authenticated === 'true';

    console.log(`🔐 Sistema de Autenticación iniciado. Estado: ${isAuthenticated ? 'AUTENTICADO' : 'INVITADO'}`);

    // 2. Asegurar que la interfaz coincida con el estado (Backup visual)
    if (isAuthenticated) {
        updateUIForAuthenticated();
    } else {
        updateUIForUnauthenticated();
    }

    // 3. Configurar listeners para botones de logout (si no tienen onclick inline)
    const logoutButtons = document.querySelectorAll('.logout-btn:not([onclick])');
    logoutButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            // Usar la función global definida en enhanced-main.js
            if (window.confirmLogout) window.confirmLogout();
        });
    });
}

function updateUIForAuthenticated() {
    // Mostrar elementos de usuario
    toggleElement("authenticatedUser", "block");
    toggleElement("welcomeMessage", "block");
    toggleElement("authenticatedInfo", "block");

    // Ocultar elementos de invitado
    toggleElement("unauthenticatedUser", "none");

    // Actualizar enlace del footer si existe
    const footerLink = document.getElementById("footerAuthLink");
    if (footerLink) footerLink.innerHTML = `<a href="/dashboard">Mi Dashboard</a>`;
}

function updateUIForUnauthenticated() {
    // Ocultar elementos de usuario
    toggleElement("authenticatedUser", "none");
    toggleElement("welcomeMessage", "none");
    toggleElement("authenticatedInfo", "none");

    // Mostrar elementos de invitado
    toggleElement("unauthenticatedUser", "flex");

    // Actualizar enlace del footer
    const footerLink = document.getElementById("footerAuthLink");
    if (footerLink) footerLink.innerHTML = `<a href="/login">Iniciar Sesión</a>`;
}

// Función auxiliar segura para mostrar/ocultar
function toggleElement(id, displayType) {
    const el = document.getElementById(id);
    if (el) {
        el.style.display = displayType;
    }
}