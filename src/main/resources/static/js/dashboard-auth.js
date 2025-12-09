/**
 * Dashboard Authentication System
 * Maneja la autenticación y sesiones en el dashboard
 */

// Variables globales
let sessionCheckInterval;
let isAuthenticated = false;
let currentUser = null;
let sessionWarningShown = false;

// Configuración
const CONFIG = {
    SESSION_CHECK_INTERVAL: 60000, // 1 minuto
    SESSION_WARNING_TIME: 300, // 5 minutos antes de expirar
    ENDPOINTS: {
        SESSION_STATUS: '/api/session/status',
        SESSION_RENEW: '/api/session/renew',
        LOGOUT: '/api/logout'
    }
};

// Inicialización cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    if (shouldInitializeAuth()) {
        initializeDashboardAuth();
    }
});

// Verificar si debemos inicializar el sistema de auth
function shouldInitializeAuth() {
    const isLocalFile = window.location.protocol === 'file:';
    const hasAuthElements =
        document.querySelector('#authenticatedUser') ||
        document.querySelector('#unauthenticatedUser');

    if (isLocalFile) return false;

    return hasAuthElements;
}

// Inicializar sistema de autenticación
function initializeDashboardAuth() {
    checkSessionStatus();

    if (window.location.protocol !== 'file:') {
        sessionCheckInterval = setInterval(checkSessionStatus, CONFIG.SESSION_CHECK_INTERVAL);
    }

    setupEventListeners();
    window.addEventListener('beforeunload', cleanup);
}

// Evento de logout
function setupEventListeners() {
    const logoutButtons = document.querySelectorAll('.logout-btn');
    logoutButtons.forEach(btn => btn.addEventListener('click', handleLogoutClick));
}

// Verificar estado de sesión
async function checkSessionStatus() {
    if (window.location.protocol === 'file:') {
        updateUIForUnauthenticated();
        return;
    }

    try {
        const response = await fetch(CONFIG.ENDPOINTS.SESSION_STATUS, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error("Respuesta inválida");

        const sessionData = await response.json();

        if (sessionData.authenticated) {
            handleAuthenticatedUser(sessionData);
        } else {
            handleUnauthenticatedUser();
        }

    } catch (error) {
        handleUnauthenticatedUser(); // Por seguridad
    }
}

// Usuario autenticado
function handleAuthenticatedUser(sessionData) {
    isAuthenticated = true;

    currentUser = {
        nombre: sessionData.usuario || "Usuario",
        rol: sessionData.rol || "Rol"
    };

    updateUIForAuthenticated(currentUser);
}

// Usuario NO autenticado
function handleUnauthenticatedUser() {
    isAuthenticated = false;
    currentUser = null;

    updateUIForUnauthenticated();
}

// ===========================
// UI: AUTENTICADO
// ===========================
function updateUIForAuthenticated(user) {

    // Mostrar contenedor del usuario autenticado
    const authUser = document.getElementById("authenticatedUser");
    if (authUser) authUser.style.display = "block";

    // Ocultar botones login/registro
    const unauth = document.getElementById("unauthenticatedUser");
    if (unauth) unauth.style.display = "none";

    // Mostrar mensaje de bienvenida
    const welcome = document.getElementById("welcomeMessage");
    if (welcome) welcome.style.display = "block";

    // Mostrar sección de info extra
    const extra = document.getElementById("authenticatedInfo");
    if (extra) extra.style.display = "block";

    // Datos de usuario en UI
    document.querySelectorAll(".user-name").forEach(el => el.textContent = user.nombre);
    document.querySelectorAll(".user-display-name").forEach(el => el.textContent = user.nombre);
    document.querySelectorAll(".welcome-name").forEach(el => el.textContent = user.nombre);

    document.querySelectorAll(".user-role").forEach(el => el.textContent = user.rol);
    document.querySelectorAll(".welcome-role").forEach(el => el.textContent = user.rol);

    // Footer
    const footerLink = document.getElementById("footerAuthLink");
    if (footerLink) footerLink.innerHTML = `<a href="/dashboard">Mi Dashboard</a>`;
}

// ===========================
// UI: NO AUTENTICADO
// ===========================
function updateUIForUnauthenticated() {

    // Esconder menú usuario
    const authUser = document.getElementById("authenticatedUser");
    if (authUser) authUser.style.display = "none";

    // Mostrar login/registro
    const unauth = document.getElementById("unauthenticatedUser");
    if (unauth) unauth.style.display = "flex";

    // Ocultar bienvenida
    const welcome = document.getElementById("welcomeMessage");
    if (welcome) welcome.style.display = "none";

    // Ocultar información extra
    const extra = document.getElementById("authenticatedInfo");
    if (extra) extra.style.display = "none";

    // Footer
    const footerLink = document.getElementById("footerAuthLink");
    if (footerLink) footerLink.innerHTML = `<a href="/login">Iniciar Sesión</a>`;
}

// ===========================
// LOGOUT
// ===========================
function handleLogoutClick(event) {
    event.preventDefault();
    confirmLogout();
}

function confirmLogout() {
    const modal = document.getElementById('logoutModal');
    if (modal) {
        const m = new bootstrap.Modal(modal);
        m.show();
    } else {
        performLogout();
    }
}

async function performLogout() {
    try {
        const response = await fetch(CONFIG.ENDPOINTS.LOGOUT, {
            method: 'POST',
            credentials: 'same-origin'
        });

        updateUIForUnauthenticated();
        cleanup();

        setTimeout(() => (window.location.href = "/index"), 700);

    } catch (err) {
        window.location.href = "/index";
    }
}

// Limpieza
function cleanup() {
    if (sessionCheckInterval) clearInterval(sessionCheckInterval);
    isAuthenticated = false;
    currentUser = null;
}

// Exportar globales
window.confirmLogout = confirmLogout;
window.performLogout = performLogout;
