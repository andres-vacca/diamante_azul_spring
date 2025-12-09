// Sistema de Autenticación Moderno - Diamante Azul
class AuthManager {
    constructor() {
        this.form = document.getElementById('loginForm');
        this.loadingOverlay = document.getElementById('loadingOverlay');
        this.alertContainer = document.getElementById('alertContainer');
        this.sessionTimeout = 30 * 60 * 1000; // 30 minutos
        this.warningTime = 5 * 60 * 1000; // 5 minutos antes
        
        this.init();
    }

    init() {
        this.setupFormValidation();
        this.setupPasswordToggle();
        this.setupForgotPassword();
        this.checkUrlParams();
        this.loadRememberedUser();
        this.startSessionMonitoring();
    }

    setupFormValidation() {
        this.form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            if (!this.form.checkValidity()) {
                e.stopPropagation();
                this.form.classList.add('was-validated');
                this.showAlert('danger', 'Por favor, complete todos los campos correctamente.');
                return;
            }

            await this.handleLogin();
        });

        // Validación en tiempo real
        const inputs = this.form.querySelectorAll('input[required]');
        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                this.validateField(input);
            });
        });
    }

    validateField(field) {
        const isValid = field.checkValidity();
        
        if (isValid) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }
        
        return isValid;
    }

    setupPasswordToggle() {
        const toggleButton = document.getElementById('togglePassword');
        const passwordInput = document.getElementById('password');

        if (toggleButton && passwordInput) {
            toggleButton.addEventListener('click', () => {
                const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordInput.setAttribute('type', type);
                
                const icon = toggleButton.querySelector('i');
                icon.classList.toggle('fa-eye');
                icon.classList.toggle('fa-eye-slash');
            });
        }
    }

    setupForgotPassword() {
        const forgotForm = document.getElementById('forgotPasswordForm');
        if (forgotForm) {
            forgotForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleForgotPassword();
            });
        }
    }

    async handleLogin() {
        const formData = new FormData(this.form);
        const credentials = {
            username: formData.get('username'),
            password: formData.get('password'),
            rememberMe: formData.get('rememberMe') === 'on'
        };

        this.showLoading(true);

        try {
            const result = await this.authenticateUser(credentials);
            
            if (result.success) {
                this.showAlert('success', 'Inicio de sesión exitoso. Redirigiendo...');
                
                // Guardar datos de sesión
                this.saveUserSession(result.user);
                
                // Recordar usuario si está marcado
                if (credentials.rememberMe) {
                    localStorage.setItem('rememberedUser', credentials.username);
                } else {
                    localStorage.removeItem('rememberedUser');
                }
                
                setTimeout(() => {
                    this.redirectToDashboard(result.user.role);
                }, 1500);
            } else {
                throw new Error(result.message);
            }

        } catch (error) {
            this.showAlert('danger', error.message);
            this.form.classList.add('was-validated');
        } finally {
            this.showLoading(false);
        }
    }

    async authenticateUser(credentials) {
        // Simular llamada al servidor
        await new Promise(resolve => setTimeout(resolve, 1500));

        // Validaciones básicas
        if (credentials.username.length < 3) {
            throw new Error('El usuario debe tener al menos 3 caracteres');
        }

        if (credentials.password.length < 6) {
            throw new Error('La contraseña debe tener al menos 6 caracteres');
        }

        // Usuarios de prueba
        const validUsers = {
            'admin': { 
                password: 'admin123', 
                role: 'admin',
                name: 'Administrador',
                email: 'admin@diamanteazul.com'
            },
            'empleado': { 
                password: 'emp123', 
                role: 'empleado',
                name: 'Empleado',
                email: 'empleado@diamanteazul.com'
            },
            'cliente': { 
                password: 'cli123', 
                role: 'cliente',
                name: 'Cliente',
                email: 'cliente@diamanteazul.com'
            }
        };

        const user = validUsers[credentials.username.toLowerCase()];
        if (!user || user.password !== credentials.password) {
            throw new Error('Usuario o contraseña incorrectos');
        }

        return {
            success: true,
            user: {
                username: credentials.username,
                name: user.name,
                email: user.email,
                role: user.role,
                loginTime: new Date().toISOString()
            }
        };
    }

    saveUserSession(user) {
        const sessionData = {
            ...user,
            sessionStart: Date.now(),
            lastActivity: Date.now()
        };
        
        sessionStorage.setItem('userSession', JSON.stringify(sessionData));
        localStorage.setItem('lastActivity', Date.now().toString());
    }

    redirectToDashboard(role) {
        const dashboardUrls = {
            'admin': '../Content/dashboard_view.html?role=admin',
            'empleado': '../Content/dashboard_view.html?role=empleado',
            'cliente': '../Content/dashboard_view.html?role=cliente'
        };

        window.location.href = dashboardUrls[role] || '../Content/dashboard_view.html';
    }

    handleForgotPassword() {
        const email = document.getElementById('recoveryEmail').value;
        
        if (email && this.validateEmail(email)) {
            this.showAlert('info', 'Se han enviado las instrucciones a tu correo electrónico');
            
            const modal = bootstrap.Modal.getInstance(document.getElementById('forgotPasswordModal'));
            modal.hide();
            
            document.getElementById('forgotPasswordForm').reset();
        } else {
            this.showAlert('danger', 'Por favor ingrese un correo electrónico válido');
        }
    }

    validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    showLoading(show) {
        const button = document.getElementById('loginButton');
        
        if (show) {
            this.loadingOverlay.classList.add('show');
            button.classList.add('loading');
            button.disabled = true;
        } else {
            this.loadingOverlay.classList.remove('show');
            button.classList.remove('loading');
            button.disabled = false;
        }
    }

    showAlert(type, message) {
        const alertHtml = `
            <div class="alert alert-custom alert-${type} alert-dismissible fade show" role="alert">
                <i class="fas fa-${this.getAlertIcon(type)} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        
        this.alertContainer.innerHTML = alertHtml;
        
        // Auto-dismiss después de 5 segundos
        setTimeout(() => {
            const alert = this.alertContainer.querySelector('.alert');
            if (alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000);
    }

    getAlertIcon(type) {
        const icons = {
            'success': 'check-circle',
            'danger': 'exclamation-triangle',
            'warning': 'exclamation-circle',
            'info': 'info-circle'
        };
        return icons[type] || 'info-circle';
    }

    checkUrlParams() {
        const urlParams = new URLSearchParams(window.location.search);
        
        if (urlParams.has('expired')) {
            this.showAlert('warning', 'Su sesión ha expirado. Por favor, inicie sesión nuevamente.');
        }
        
        if (urlParams.has('logout')) {
            this.showAlert('info', 'Ha cerrado sesión correctamente.');
        }
        
        if (urlParams.has('unauthorized')) {
            this.showAlert('danger', 'No tiene permisos para acceder a esa sección.');
        }
        
        if (urlParams.has('registered')) {
            this.showAlert('success', 'Registro exitoso. Ya puede iniciar sesión.');
        }
    }

    loadRememberedUser() {
        const rememberedUser = localStorage.getItem('rememberedUser');
        if (rememberedUser) {
            document.getElementById('username').value = rememberedUser;
            document.getElementById('rememberMe').checked = true;
        }
    }

    startSessionMonitoring() {
        // Solo si estamos en una página que requiere autenticación
        if (window.location.pathname.includes('dashboard') || 
            document.body.classList.contains('dashboard')) {
            
            this.monitorSession();
            this.setupActivityTracking();
        }
    }

    monitorSession() {
        setInterval(() => {
            const session = this.getSession();
            if (session) {
                const timeElapsed = Date.now() - session.sessionStart;
                const timeRemaining = this.sessionTimeout - timeElapsed;
                
                if (timeRemaining <= 0) {
                    this.handleSessionExpired();
                } else if (timeRemaining <= this.warningTime) {
                    this.showSessionWarning(timeRemaining);
                }
            }
        }, 60000); // Verificar cada minuto
    }

    setupActivityTracking() {
        const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
        
        events.forEach(event => {
            document.addEventListener(event, () => {
                this.updateLastActivity();
            }, true);
        });
    }

    updateLastActivity() {
        const session = this.getSession();
        if (session) {
            session.lastActivity = Date.now();
            sessionStorage.setItem('userSession', JSON.stringify(session));
            localStorage.setItem('lastActivity', Date.now().toString());
        }
    }

    getSession() {
        const sessionData = sessionStorage.getItem('userSession');
        return sessionData ? JSON.parse(sessionData) : null;
    }

    showSessionWarning(timeRemaining) {
        const minutes = Math.floor(timeRemaining / 60000);
        
        if (!document.querySelector('.session-warning')) {
            const warningHtml = `
                <div class="session-warning position-fixed top-0 start-50 translate-middle-x mt-3" style="z-index: 9999;">
                    <div class="alert alert-warning alert-dismissible" role="alert">
                        <i class="fas fa-clock me-2"></i>
                        Su sesión expirará en ${minutes} minutos.
                        <button type="button" class="btn btn-sm btn-outline-warning ms-2" onclick="authManager.renewSession()">
                            Extender
                        </button>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </div>
            `;
            
            document.body.insertAdjacentHTML('beforeend', warningHtml);
        }
    }

    renewSession() {
        const session = this.getSession();
        if (session) {
            session.sessionStart = Date.now();
            session.lastActivity = Date.now();
            sessionStorage.setItem('userSession', JSON.stringify(session));
            
            // Remover advertencia
            const warning = document.querySelector('.session-warning');
            if (warning) {
                warning.remove();
            }
            
            this.showAlert('success', 'Sesión renovada correctamente');
        }
    }

    handleSessionExpired() {
        this.clearSession();
        alert('Su sesión ha expirado. Será redirigido al login.');
        window.location.href = '../forms/Login.html';
    }

    clearSession() {
        sessionStorage.removeItem('userSession');
        localStorage.removeItem('lastActivity');
    }
}

// Función global para mostrar modal de recuperación
function showForgotPassword() {
    const modal = new bootstrap.Modal(document.getElementById('forgotPasswordModal'));
    modal.show();
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    window.authManager = new AuthManager();
});

// Exportar para uso global
window.AuthManager = AuthManager;