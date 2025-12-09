// Dashboard JavaScript - Diamante Azul
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
});

function initializeDashboard() {
    // Inicializar navegación del sidebar
    initializeSidebarNavigation();
    
    // Inicializar contadores animados
    animateCounters();
    
    // Inicializar formularios
    initializeForms();
    
    // Inicializar búsquedas en tablas
    initializeTableSearch();
    
    // Cargar datos iniciales
    loadDashboardData();
    
    // Configurar eventos
    setupEventListeners();
}

// Navegación del Sidebar
function initializeSidebarNavigation() {
    const sidebarLinks = document.querySelectorAll('.sidebar .nav-link');
    const contentSections = document.querySelectorAll('.content-section');
    
    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remover clase active de todos los links
            sidebarLinks.forEach(l => l.classList.remove('active'));
            
            // Agregar clase active al link clickeado
            this.classList.add('active');
            
            // Obtener la sección objetivo
            const targetSection = this.getAttribute('data-section');
            
            // Mostrar la sección correspondiente
            showSection(targetSection);
        });
    });
}

// Mostrar sección específica
function showSection(sectionName, action = null) {
    const contentSections = document.querySelectorAll('.content-section');
    
    // Ocultar todas las secciones
    contentSections.forEach(section => {
        section.classList.remove('active');
    });
    
    // Mostrar la sección objetivo
    const targetSection = document.getElementById(`${sectionName}-section`);
    if (targetSection) {
        targetSection.classList.add('active');
        
        // Si hay una acción específica (como 'crear'), mostrar el formulario
        if (action === 'crear') {
            setTimeout(() => {
                if (sectionName === 'usuarios') showUserForm();
                else if (sectionName === 'clientes') showClienteForm();
                else if (sectionName === 'productos') showProductoForm();
                else if (sectionName === 'empenos') showEmpenoForm();
            }, 300);
        }
    }
    
    // Actualizar navegación del sidebar
    const sidebarLinks = document.querySelectorAll('.sidebar .nav-link');
    sidebarLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('data-section') === sectionName) {
            link.classList.add('active');
        }
    });
}

// Animación de contadores
function animateCounters() {
    const counters = document.querySelectorAll('.counter');
    
    counters.forEach(counter => {
        const target = parseInt(counter.getAttribute('data-target'));
        const increment = target / 100;
        let current = 0;
        
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                counter.textContent = target;
                clearInterval(timer);
            } else {
                counter.textContent = Math.floor(current);
            }
        }, 20);
    });
}

// Gestión de Usuarios
function showUserForm() {
    const form = document.getElementById('usuarios-form');
    if (form) {
        form.style.display = 'block';
        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        document.getElementById('usuarioNombre').focus();
    }
}

function hideUserForm() {
    const form = document.getElementById('usuarios-form');
    if (form) {
        form.style.display = 'none';
        document.getElementById('usuarioForm').reset();
    }
}

function editUsuario(id) {
    showUserForm();
    // Aquí cargarías los datos del usuario para editar
    showInfo(`Editando usuario ID: ${id}`);
}

function deleteUsuario(id) {
    if (confirmAction('¿Estás seguro de eliminar este usuario?', 'Eliminar Usuario')) {
        // Aquí harías la llamada para eliminar
        showSuccess('Usuario eliminado correctamente');
        refreshUsuarios();
    }
}

function refreshUsuarios() {
    showInfo('Actualizando lista de usuarios...');
    // Aquí harías la llamada para actualizar la tabla
}

// Gestión de Clientes
function showClienteForm() {
    const form = document.getElementById('clientes-form');
    if (form) {
        form.style.display = 'block';
        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        document.getElementById('clienteNombre').focus();
    }
}

function hideClienteForm() {
    const form = document.getElementById('clientes-form');
    if (form) {
        form.style.display = 'none';
        document.getElementById('clienteForm').reset();
    }
}

function editCliente(id) {
    showClienteForm();
    showInfo(`Editando cliente ID: ${id}`);
}

function deleteCliente(id) {
    if (confirmAction('¿Estás seguro de eliminar este cliente?', 'Eliminar Cliente')) {
        showSuccess('Cliente eliminado correctamente');
        refreshClientes();
    }
}

function refreshClientes() {
    showInfo('Actualizando lista de clientes...');
}

// Gestión de Productos
function showProductoForm() {
    const form = document.getElementById('productos-form');
    if (form) {
        form.style.display = 'block';
        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        document.getElementById('productoNombre').focus();
    }
}

function hideProductoForm() {
    const form = document.getElementById('productos-form');
    if (form) {
        form.style.display = 'none';
        document.getElementById('productoForm').reset();
    }
}

function editProducto(id) {
    showProductoForm();
    showInfo(`Editando producto ID: ${id}`);
}

function deleteProducto(id) {
    if (confirmAction('¿Estás seguro de eliminar este producto?', 'Eliminar Producto')) {
        showSuccess('Producto eliminado correctamente');
        refreshProductos();
    }
}

function refreshProductos() {
    showInfo('Actualizando lista de productos...');
}

// Gestión de Empeños
function showEmpenoForm() {
    const form = document.getElementById('empenos-form');
    if (form) {
        form.style.display = 'block';
        form.scrollIntoView({ behavior: 'smooth', block: 'start' });
        document.getElementById('empenoProducto').focus();
    }
}

function hideEmpenoForm() {
    const form = document.getElementById('empenos-form');
    if (form) {
        form.style.display = 'none';
        document.getElementById('empenoForm').reset();
    }
}

function editEmpeno(id) {
    showEmpenoForm();
    showInfo(`Editando empeño ID: ${id}`);
}

function renovarEmpeno(id) {
    if (confirmAction('¿Deseas renovar este empeño?', 'Renovar Empeño')) {
        showSuccess('Empeño renovado correctamente');
        refreshEmpenos();
    }
}

function pagarEmpeno(id) {
    if (confirmAction('¿Confirmas el pago de este empeño?', 'Pagar Empeño')) {
        showSuccess('Empeño pagado correctamente');
        refreshEmpenos();
    }
}

function refreshEmpenos() {
    showInfo('Actualizando lista de empeños...');
}

// Inicializar formularios
function initializeForms() {
    // Formulario de Usuarios
    const usuarioForm = document.getElementById('usuarioForm');
    if (usuarioForm) {
        usuarioForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                const formData = new FormData(this);
                const data = Object.fromEntries(formData);
                
                showLoading(this.querySelector('button[type="submit"]'));
                
                // Simular envío
                setTimeout(() => {
                    hideLoading(this.querySelector('button[type="submit"]'));
                    showSuccess('Usuario guardado correctamente');
                    hideUserForm();
                    refreshUsuarios();
                }, 1500);
            }
            this.classList.add('was-validated');
        });
    }
    
    // Formulario de Clientes
    const clienteForm = document.getElementById('clienteForm');
    if (clienteForm) {
        clienteForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                const formData = new FormData(this);
                const data = Object.fromEntries(formData);
                
                showLoading(this.querySelector('button[type="submit"]'));
                
                setTimeout(() => {
                    hideLoading(this.querySelector('button[type="submit"]'));
                    showSuccess('Cliente guardado correctamente');
                    hideClienteForm();
                    refreshClientes();
                }, 1500);
            }
            this.classList.add('was-validated');
        });
    }
    
    // Formulario de Productos
    const productoForm = document.getElementById('productoForm');
    if (productoForm) {
        productoForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                const formData = new FormData(this);
                const data = Object.fromEntries(formData);
                
                showLoading(this.querySelector('button[type="submit"]'));
                
                setTimeout(() => {
                    hideLoading(this.querySelector('button[type="submit"]'));
                    showSuccess('Producto guardado correctamente');
                    hideProductoForm();
                    refreshProductos();
                }, 1500);
            }
            this.classList.add('was-validated');
        });
    }
    
    // Formulario de Empeños
    const empenoForm = document.getElementById('empenoForm');
    if (empenoForm) {
        empenoForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                const formData = new FormData(this);
                const data = Object.fromEntries(formData);
                
                showLoading(this.querySelector('button[type="submit"]'));
                
                setTimeout(() => {
                    hideLoading(this.querySelector('button[type="submit"]'));
                    showSuccess('Empeño registrado correctamente');
                    hideEmpenoForm();
                    refreshEmpenos();
                }, 1500);
            }
            this.classList.add('was-validated');
        });
    }
}

// Búsqueda en tablas mejorada
function initializeTableSearch() {
    const searchInputs = document.querySelectorAll('.table-search');
    
    searchInputs.forEach(input => {
        input.addEventListener('keyup', debounce(function() {
            const searchTerm = this.value.toLowerCase();
            const tableId = this.getAttribute('data-table');
            const table = document.getElementById(tableId);
            
            if (table) {
                const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
                let visibleCount = 0;
                
                Array.from(rows).forEach(row => {
                    // Excluir filas de mensaje de resultados
                    if (row.classList.contains('search-results-message')) {
                        return;
                    }
                    
                    const text = row.textContent.toLowerCase();
                    const isVisible = text.includes(searchTerm);
                    row.style.display = isVisible ? '' : 'none';
                    if (isVisible) visibleCount++;
                });
                
                updateSearchResults(table, visibleCount, searchTerm);
            }
        }, 300));
    });
}

// Actualizar resultados de búsqueda
function updateSearchResults(table, count, searchTerm) {
    let messageRow = table.querySelector('.search-results-message');
    
    if (messageRow) {
        messageRow.remove();
    }
    
    if (count === 0 && searchTerm) {
        const tbody = table.getElementsByTagName('tbody')[0];
        const colCount = table.getElementsByTagName('thead')[0].getElementsByTagName('th').length;
        
        messageRow = document.createElement('tr');
        messageRow.className = 'search-results-message';
        messageRow.innerHTML = `
            <td colspan="${colCount}" class="text-center text-muted py-4">
                <i class="fas fa-search me-2"></i>
                No se encontraron resultados para "${searchTerm}"
            </td>
        `;
        tbody.appendChild(messageRow);
    }
}

// Cargar datos del dashboard
function loadDashboardData() {
    // Simular carga de datos
    setTimeout(() => {
        // Formatear monedas
        const currencyElements = document.querySelectorAll('.currency-format');
        currencyElements.forEach(element => {
            const value = parseFloat(element.textContent.replace(/[^0-9.-]+/g, ''));
            if (!isNaN(value)) {
                element.textContent = formatCurrency(value);
            }
        });
    }, 500);
}

// Configurar event listeners adicionales
function setupEventListeners() {
    // Botón de actualizar dashboard
    const refreshBtn = document.querySelector('[onclick="refreshDashboard()"]');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', refreshDashboard);
    }
    
    // Botón de exportar reporte
    const exportBtn = document.querySelector('[onclick="exportReport()"]');
    if (exportBtn) {
        exportBtn.addEventListener('click', exportReport);
    }
    
    // Responsive sidebar toggle
    createSidebarToggle();
}

// Crear botón toggle para sidebar en mobile
function createSidebarToggle() {
    if (window.innerWidth <= 768) {
        let toggleBtn = document.querySelector('.sidebar-toggle');
        if (!toggleBtn) {
            toggleBtn = document.createElement('button');
            toggleBtn.className = 'sidebar-toggle';
            toggleBtn.innerHTML = '<i class="fas fa-bars"></i>';
            document.body.appendChild(toggleBtn);
            
            toggleBtn.addEventListener('click', function() {
                const sidebar = document.querySelector('.sidebar');
                sidebar.classList.toggle('show');
            });
        }
    }
}

// Funciones de utilidad del dashboard
function refreshDashboard() {
    showInfo('Actualizando dashboard...');
    
    // Simular actualización
    setTimeout(() => {
        animateCounters();
        loadDashboardData();
        showSuccess('Dashboard actualizado correctamente');
    }, 1000);
}

function exportReport() {
    showInfo('Generando reporte...');
    
    // Simular generación de reporte
    setTimeout(() => {
        const data = [
            { tipo: 'Usuarios', cantidad: 150, estado: 'Activo' },
            { tipo: 'Clientes', cantidad: 89, estado: 'Activo' },
            { tipo: 'Productos', cantidad: 245, estado: 'Disponible' },
            { tipo: 'Empeños', cantidad: 67, estado: 'Activo' }
        ];
        
        downloadCSV(data, 'reporte-dashboard.csv');
        showSuccess('Reporte exportado correctamente');
    }, 1500);
}

// Manejo de responsive
window.addEventListener('resize', function() {
    createSidebarToggle();
    
    if (window.innerWidth > 768) {
        const sidebar = document.querySelector('.sidebar');
        sidebar.classList.remove('show');
    }
});

// Cerrar sidebar al hacer click fuera (mobile)
document.addEventListener('click', function(e) {
    if (window.innerWidth <= 768) {
        const sidebar = document.querySelector('.sidebar');
        const toggleBtn = document.querySelector('.sidebar-toggle');
        
        if (!sidebar.contains(e.target) && !toggleBtn.contains(e.target)) {
            sidebar.classList.remove('show');
        }
    }
});

// Funciones globales para el dashboard
window.showSection = showSection;
window.showUserForm = showUserForm;
window.hideUserForm = hideUserForm;
window.editUsuario = editUsuario;
window.deleteUsuario = deleteUsuario;
window.refreshUsuarios = refreshUsuarios;

window.showClienteForm = showClienteForm;
window.hideClienteForm = hideClienteForm;
window.editCliente = editCliente;
window.deleteCliente = deleteCliente;
window.refreshClientes = refreshClientes;

window.showProductoForm = showProductoForm;
window.hideProductoForm = hideProductoForm;
window.editProducto = editProducto;
window.deleteProducto = deleteProducto;
window.refreshProductos = refreshProductos;

window.showEmpenoForm = showEmpenoForm;
window.hideEmpenoForm = hideEmpenoForm;
window.editEmpeno = editEmpeno;
window.renovarEmpeno = renovarEmpeno;
window.pagarEmpeno = pagarEmpeno;
window.refreshEmpenos = refreshEmpenos;

window.refreshDashboard = refreshDashboard;
window.exportReport = exportReport;

// Inicialización cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeDashboard);
} else {
    initializeDashboard();
}