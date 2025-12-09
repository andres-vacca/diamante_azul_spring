/**
 * Enhanced Main JavaScript for Diamante Azul
 * Funcionalidades mejoradas y optimizadas
 */

// Configuración global
const CONFIG = {
    theme: {
        storageKey: 'diamante-theme',
        defaultTheme: 'light'
    },
    animations: {
        scrollOffset: 100,
        counterDuration: 2000,
        alertDuration: 5000
    },
    auth: {
        checkInterval: 30000, // 30 segundos
        sessionWarning: 300000 // 5 minutos
    }
};

// Estado global de la aplicación
const AppState = {
    isAuthenticated: false,
    currentTheme: 'light',
    user: null,
    animations: {
        countersStarted: false
    }
};

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

/**
 * Inicializar la aplicación
 */
function initializeApp() {
    console.log('🚀 Inicializando Diamante Azul...');
    
    // Inicializar componentes principales
    initializeTheme();
    initializeAuth();
    initializeAnimations();
    initializeComponents();
    initializeEventListeners();
    
    console.log('✅ Aplicación inicializada correctamente');
}

/**
 * ===================================
 * GESTIÓN DE TEMA
 * ===================================
 */

function initializeTheme() {
    const savedTheme = localStorage.getItem(CONFIG.theme.storageKey) || CONFIG.theme.defaultTheme;
    const html = document.documentElement;
    const body = document.body;
    const themeToggle = document.getElementById('themeToggle');
    const themeIcon = document.getElementById('themeIcon');
    
    // Aplicar tema guardado
    html.setAttribute('data-theme', savedTheme);
    body.setAttribute('data-theme', savedTheme);
    AppState.currentTheme = savedTheme;
    updateThemeIcon(savedTheme);
    
    // Event listener para el toggle de tema
    if (themeToggle) {
        themeToggle.addEventListener('click', function(e) {
            e.preventDefault();
            toggleTheme();
        });
    }
    
    // Detectar cambios en preferencia del sistema
    if (window.matchMedia) {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        mediaQuery.addEventListener('change', handleSystemThemeChange);
    }
}

function toggleTheme() {
    const html = document.documentElement;
    const body = document.body;
    const currentTheme = AppState.currentTheme;
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    // Añadir clase de transición
    body.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
    
    // Cambiar tema
    html.setAttribute('data-theme', newTheme);
    body.setAttribute('data-theme', newTheme);
    localStorage.setItem(CONFIG.theme.storageKey, newTheme);
    AppState.currentTheme = newTheme;
    
    // Actualizar icono
    updateThemeIcon(newTheme);
    
    // Mostrar notificación
    showAlert(
        `Tema ${newTheme === 'dark' ? 'oscuro' : 'claro'} activado`,
        'info',
        2000
    );
    
    // Remover transición después del cambio
    setTimeout(() => {
        body.style.transition = '';
    }, 300);
}

function updateThemeIcon(theme) {
    const themeIcon = document.getElementById('themeIcon');
    if (themeIcon) {
        themeIcon.className = theme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
}

function handleSystemThemeChange(e) {
    const systemTheme = e.matches ? 'dark' : 'light';
    const savedTheme = localStorage.getItem(CONFIG.theme.storageKey);
    
    // Solo cambiar si no hay preferencia guardada
    if (!savedTheme) {
        const html = document.documentElement;
        const body = document.body;
        
        html.setAttribute('data-theme', systemTheme);
        body.setAttribute('data-theme', systemTheme);
        AppState.currentTheme = systemTheme;
        updateThemeIcon(systemTheme);
    }
}

/**
 * ===================================
 * GESTIÓN DE AUTENTICACIÓN
 * ===================================
 */

function initializeAuth() {
    // Verificar estado inicial de autenticación
    checkAuthStatus();
    
    // Configurar verificación periódica
    setInterval(checkAuthStatus, CONFIG.auth.checkInterval);
}

async function checkAuthStatus() {
    try {
        // Verificar estado local basado en renderizado del servidor
        const isAuthenticated = checkLocalAuthStatus();
        
        if (isAuthenticated !== AppState.isAuthenticated) {
            AppState.isAuthenticated = isAuthenticated;
            updateAuthUI(isAuthenticated);
        }
    } catch (error) {
        console.error('Error verificando estado de autenticación:', error);
    }
}

function checkLocalAuthStatus() {
    // Verificar si el servidor indicó que el usuario está autenticado
    // Esto se establece mediante th:data-authenticated en el body
    if (document.body.dataset.authenticated === 'true') {
        return true;
    }
    
    // Fallback: NO verificar elementos DOM ocultos, ya que siempre existen
    return false;
}

function updateAuthUI(isAuthenticated) {
    const authenticatedUser = document.getElementById('authenticatedUser');
    const unauthenticatedUser = document.getElementById('unauthenticatedUser');
    const welcomeMessage = document.getElementById('welcomeMessage');
    const authenticatedInfo = document.getElementById('authenticatedInfo');
    const footerAuthLink = document.getElementById('footerAuthLink');
    
    if (isAuthenticated) {
        // Mostrar elementos para usuarios autenticados
        if (authenticatedUser) authenticatedUser.style.display = 'block';
        if (unauthenticatedUser) unauthenticatedUser.style.display = 'none';
        if (welcomeMessage) welcomeMessage.style.display = 'block';
        if (authenticatedInfo) authenticatedInfo.style.display = 'block';
        
        // Actualizar enlace del footer
        if (footerAuthLink) {
            footerAuthLink.innerHTML = '<a href="/dashboard">Dashboard</a>';
        }
    } else {
        // Mostrar elementos para usuarios no autenticados
        if (authenticatedUser) authenticatedUser.style.display = 'none';
        if (unauthenticatedUser) unauthenticatedUser.style.display = 'flex';
        if (welcomeMessage) welcomeMessage.style.display = 'none';
        if (authenticatedInfo) authenticatedInfo.style.display = 'none';
        
        // Actualizar enlace del footer
        if (footerAuthLink) {
            footerAuthLink.innerHTML = '<a href="/login">Iniciar Sesión</a>';
        }
    }
}

/**
 * ===================================
 * ANIMACIONES
 * ===================================
 */

function initializeAnimations() {
    setupScrollAnimations();
    setupCounterAnimations();
    setupParallaxEffects();
}

function setupScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animated');
                
                // Añadir delay escalonado para múltiples elementos
                const delay = Array.from(entry.target.parentNode.children).indexOf(entry.target) * 100;
                entry.target.style.animationDelay = `${delay}ms`;
                
                observer.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });
    
    document.querySelectorAll('.animate-on-scroll').forEach(el => {
        observer.observe(el);
    });
}

function setupCounterAnimations() {
    const counterElements = document.querySelectorAll('.stat-number');
    
    const counterObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && !AppState.animations.countersStarted) {
                AppState.animations.countersStarted = true;
                animateCounters();
                counterObserver.unobserve(entry.target);
            }
        });
    });
    
    if (counterElements.length > 0) {
        counterObserver.observe(counterElements[0]);
    }
}

function animateCounters() {
    const counters = document.querySelectorAll('.stat-number');
    
    counters.forEach(counter => {
        const target = parseInt(counter.dataset.count) || parseInt(counter.textContent);
        const duration = CONFIG.animations.counterDuration;
        const increment = target / (duration / 16); // 60fps
        let current = 0;
        
        const updateCounter = () => {
            current += increment;
            if (current >= target) {
                counter.textContent = target + (target === 100 ? '%' : target >= 1000 ? '+' : '');
            } else {
                counter.textContent = Math.floor(current) + (target === 100 ? '%' : target >= 1000 ? '+' : '');
                requestAnimationFrame(updateCounter);
            }
        };
        
        updateCounter();
    });
}

function setupParallaxEffects() {
    const heroParticles = document.querySelector('.hero-particles');
    
    if (heroParticles) {
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const rate = scrolled * -0.5;
            heroParticles.style.transform = `translateY(${rate}px)`;
        });
    }
}

/**
 * ===================================
 * COMPONENTES
 * ===================================
 */

function initializeComponents() {
    initializeTooltips();
    initializePopovers();
    initializeSmoothScrolling();
    initializeForms();
    initializeNavbar();
}

function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function initializePopovers() {
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}

function initializeSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                const headerOffset = 80;
                const elementPosition = target.getBoundingClientRect().top;
                const offsetPosition = elementPosition + window.pageYOffset - headerOffset;
                
                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

function initializeForms() {
    const contactForm = document.getElementById('contactForm');
    if (contactForm) {
        contactForm.addEventListener('submit', handleContactFormSubmit);
    }
    
    // Validación en tiempo real
    const inputs = document.querySelectorAll('input[type="email"], input[type="tel"]');
    inputs.forEach(input => {
        input.addEventListener('blur', validateInput);
        input.addEventListener('input', clearValidationErrors);
    });
}

function initializeNavbar() {
    const navbar = document.querySelector('.navbar');
    
    // Cambiar apariencia del navbar al hacer scroll
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
    
    // Cerrar navbar móvil al hacer clic en un enlace
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');
    
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (navbarCollapse.classList.contains('show')) {
                navbarToggler.click();
            }
        });
    });
}

/**
 * ===================================
 * EVENT LISTENERS
 * ===================================
 */

function initializeEventListeners() {
    // Auto-cerrar alertas
    setTimeout(() => {
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(alert => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        });
    }, CONFIG.animations.alertDuration);
    
    // Manejar errores globales
    window.addEventListener('error', handleGlobalError);
    window.addEventListener('unhandledrejection', handleUnhandledRejection);
    
    // Optimizar rendimiento en scroll
    let ticking = false;
    window.addEventListener('scroll', () => {
        if (!ticking) {
            requestAnimationFrame(() => {
                handleScroll();
                ticking = false;
            });
            ticking = true;
        }
    });
}

/**
 * ===================================
 * MANEJADORES DE EVENTOS
 * ===================================
 */

async function handleContactFormSubmit(e) {
    e.preventDefault();
    
    const form = e.target;
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    
    // Validar formulario
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        showAlert('Por favor, complete todos los campos requeridos', 'warning');
        return;
    }
    
    // Mostrar estado de carga
    showLoading(submitBtn, 'Enviando...');
    
    try {
        // Simular envío (en producción sería una llamada AJAX real)
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // Éxito
        showAlert('¡Mensaje enviado correctamente! Te contactaremos pronto.', 'success');
        form.reset();
        form.classList.remove('was-validated');
        
    } catch (error) {
        console.error('Error enviando formulario:', error);
        showAlert('Error al enviar el mensaje. Por favor, inténtalo de nuevo.', 'danger');
    } finally {
        hideLoading(submitBtn, originalText);
    }
}

function validateInput(e) {
    const input = e.target;
    const type = input.type;
    const value = input.value.trim();
    
    if (type === 'email' && value) {
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
        toggleInputValidation(input, isValid, 'Por favor, ingrese un email válido');
    } else if (type === 'tel' && value) {
        const isValid = /^[\+]?[0-9\s\-\(\)]{10,}$/.test(value);
        toggleInputValidation(input, isValid, 'Por favor, ingrese un teléfono válido');
    }
}

function clearValidationErrors(e) {
    const input = e.target;
    input.classList.remove('is-invalid');
    input.setCustomValidity('');
}

function toggleInputValidation(input, isValid, errorMessage) {
    if (isValid) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        input.setCustomValidity('');
    } else {
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        input.setCustomValidity(errorMessage);
    }
}

function handleScroll() {
    const scrollY = window.scrollY;
    
    // Actualizar navbar
    const navbar = document.querySelector('.navbar');
    if (navbar) {
        if (scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    }
}

function handleGlobalError(event) {
    console.error('Error global:', event.error);
    // showAlert('Ha ocurrido un error inesperado', 'danger');
}

function handleUnhandledRejection(event) {
    console.error('Promise rechazada:', event.reason);
    // showAlert('Error de conexión', 'warning');
}

/**
 * ===================================
 * FUNCIONES DE AUTENTICACIÓN
 * ===================================
 */

function confirmLogout() {
    const modal = document.getElementById('logoutModal');
    if (modal) {
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    }
}

async function performLogout() {
    const modal = document.getElementById('logoutModal');
    const confirmBtn = modal.querySelector('.btn-danger');
    
    // Mostramos estado de carga en el botón
    showLoading(confirmBtn, 'Cerrando...');
    
    // Buscamos el formulario oculto que pusimos en el HTML
    const logoutForm = document.getElementById('logoutForm');
    
    if (logoutForm) {
        // Enviamos el formulario. 
        // Esto hará la petición POST a /logout y Spring redirigirá al index automáticamente.
        logoutForm.submit();
    } else {
        console.error('Error: No se encontró el formulario de logout');
        // Fallback de emergencia
        window.location.href = '/login?logout=true';
    }
}

function showProfile() {
    showAlert('Funcionalidad de perfil en desarrollo', 'info');
}

/**
 * ===================================
 * UTILIDADES
 * ===================================
 */

function showAlert(message, type = 'info', duration = CONFIG.animations.alertDuration) {
    const alertContainer = document.getElementById('alertContainer') || createAlertContainer();
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.setAttribute('role', 'alert');
    
    const icons = {
        success: 'fas fa-check-circle',
        danger: 'fas fa-exclamation-triangle',
        warning: 'fas fa-exclamation-circle',
        info: 'fas fa-info-circle'
    };
    
    alertDiv.innerHTML = `
        <i class="${icons[type] || icons.info} me-2"></i>
        <span>${message}</span>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    alertContainer.appendChild(alertDiv);
    
    // Auto-remover
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.classList.remove('show');
            setTimeout(() => alertDiv.remove(), 150);
        }
    }, duration);
    
    return alertDiv;
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.id = 'alertContainer';
    container.className = 'alert-container';
    document.body.appendChild(container);
    return container;
}

function showLoading(element, text = 'Cargando...') {
    if (element) {
        element.disabled = true;
        element.dataset.originalText = element.innerHTML;
        element.innerHTML = `
            <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            ${text}
        `;
    }
}

function hideLoading(element, originalText = null) {
    if (element) {
        element.disabled = false;
        element.innerHTML = originalText || element.dataset.originalText || 'Enviar';
        delete element.dataset.originalText;
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(amount);
}

function formatDate(date, options = {}) {
    const defaultOptions = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    };
    
    return new Intl.DateTimeFormat('es-CO', { ...defaultOptions, ...options }).format(new Date(date));
}

function debounce(func, wait, immediate) {
    let timeout;
    return function executedFunction() {
        const context = this;
        const args = arguments;
        
        const later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        
        if (callNow) func.apply(context, args);
    };
}

/**
 * ===================================
 * EXPORTAR FUNCIONES GLOBALES
 * ===================================
 */

// Funciones disponibles globalmente
window.DiamanteFunctions = {
    showAlert,
    confirmLogout,
    performLogout,
    showProfile,
    toggleTheme,
    formatCurrency,
    formatDate,
    showLoading,
    hideLoading,
    debounce
};

// Compatibilidad con scripts existentes
window.showAlert = showAlert;
window.confirmLogout = confirmLogout;
window.performLogout = performLogout;
window.showProfile = showProfile;
window.showNotification = showAlert; // Alias
window.showSuccess = (msg, duration) => showAlert(msg, 'success', duration);
window.showError = (msg, duration) => showAlert(msg, 'danger', duration);
window.showWarning = (msg, duration) => showAlert(msg, 'warning', duration);
window.showInfo = (msg, duration) => showAlert(msg, 'info', duration);

console.log('✅ Enhanced Main JavaScript cargado correctamente');