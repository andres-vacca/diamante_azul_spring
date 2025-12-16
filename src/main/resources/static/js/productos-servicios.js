// JavaScript para la página de productos y servicios

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar filtros de productos
    initializeProductFilters();

    // Inicializar animaciones
    initializeScrollAnimations();

    // Inicializar efectos de hover
    initializeHoverEffects();
});

// Filtros de productos
function initializeProductFilters() {
    const filterButtons = document.querySelectorAll('[data-filter]');
    const productItems = document.querySelectorAll('.product-item');

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const filter = this.getAttribute('data-filter');

            // Actualizar botones activos
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');

            // Filtrar productos
            productItems.forEach(item => {
                const category = item.getAttribute('data-category');

                if (filter === 'all' || category === filter) {
                    item.style.display = 'block';
                    item.style.animation = 'fadeInUp 0.6s ease forwards';
                } else {
                    item.style.display = 'none';
                }
            });
        });
    });
}

// Animaciones al hacer scroll
function initializeScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');

                // Animación especial para cards
                if (entry.target.classList.contains('product-card') ||
                    entry.target.classList.contains('service-card')) {
                    entry.target.style.animationDelay = Math.random() * 0.3 + 's';
                }
            }
        });
    }, observerOptions);

    // Observar elementos
    document.querySelectorAll('.card, .product-item, .service-card').forEach(el => {
        observer.observe(el);
    });
}

// Efectos de hover mejorados
function initializeHoverEffects() {
    // Efecto parallax en hero
    const hero = document.querySelector('.hero-products');
    if (hero) {
        window.addEventListener('scroll', function() {
            const scrolled = window.pageYOffset;
            const rate = scrolled * -0.5;
            hero.style.transform = `translateY(${rate}px)`;
        });
    }

    // Efecto de brillo en botones
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 0.5rem 1rem rgba(0, 0, 0, 0.15)';
        });

        button.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '';
        });
    });

    // Efecto de rotación en iconos de servicio
    const serviceIcons = document.querySelectorAll('.service-icon');
    serviceIcons.forEach(icon => {
        icon.addEventListener('mouseenter', function() {
            this.style.transform = 'rotate(10deg) scale(1.1)';
        });

        icon.addEventListener('mouseleave', function() {
            this.style.transform = 'rotate(0deg) scale(1)';
        });
    });
}

// Función para mostrar detalles del producto
function showProductDetails(productId) {
    // Aquí se puede implementar un modal o redirección
    showNotification('Función de detalles del producto en desarrollo', 'info');
}

// Función para agregar al carrito
function addToCart(productId) {
    // Simular agregar al carrito
    showNotification('Producto agregado al carrito', 'success');

    // Aquí se puede implementar la lógica real del carrito
    console.log('Producto agregado:', productId);
}

// Función para solicitar cotización de servicio
function requestQuote(serviceType) {
    // Simular solicitud de cotización
    showNotification(`Cotización solicitada para: ${serviceType}`, 'success');

    // Aquí se puede implementar un formulario de cotización
    console.log('Cotización solicitada:', serviceType);
}

// Función para búsqueda de productos
function searchProducts(query) {
    const productItems = document.querySelectorAll('.product-item');
    const searchTerm = query.toLowerCase();

    productItems.forEach(item => {
        const title = item.querySelector('.card-title').textContent.toLowerCase();
        const description = item.querySelector('.card-text').textContent.toLowerCase();

        if (title.includes(searchTerm) || description.includes(searchTerm)) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

// Función para ordenar productos
function sortProducts(criteria) {
    const container = document.getElementById('productos-grid');
    const items = Array.from(container.querySelectorAll('.product-item'));

    items.sort((a, b) => {
        let aValue, bValue;

        switch (criteria) {
            case 'price-low':
                aValue = parseInt(a.querySelector('.h5').textContent.replace(/[^0-9]/g, ''));
                bValue = parseInt(b.querySelector('.h5').textContent.replace(/[^0-9]/g, ''));
                return aValue - bValue;
            case 'price-high':
                aValue = parseInt(a.querySelector('.h5').textContent.replace(/[^0-9]/g, ''));
                bValue = parseInt(b.querySelector('.h5').textContent.replace(/[^0-9]/g, ''));
                return bValue - aValue;
            case 'name':
                aValue = a.querySelector('.card-title').textContent;
                bValue = b.querySelector('.card-title').textContent;
                return aValue.localeCompare(bValue);
            default:
                return 0;
        }
    });

    // Reordenar elementos en el DOM
    items.forEach(item => container.appendChild(item));
}

// Función para lazy loading de imágenes
function initializeLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');

    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.classList.remove('lazy');
                imageObserver.unobserve(img);
            }
        });
    });

    images.forEach(img => imageObserver.observe(img));
}

// Función para compartir producto
function shareProduct(productName, productUrl) {
    if (navigator.share) {
        navigator.share({
            title: productName,
            text: `Mira este producto: ${productName}`,
            url: productUrl
        });
    } else {
        // Fallback para navegadores que no soportan Web Share API
        copyToClipboard(productUrl);
        showNotification('Enlace copiado al portapapeles', 'success');
    }
}

// Función para comparar productos
let compareList = [];

function addToCompare(productId) {
    if (compareList.length >= 3) {
        showNotification('Solo puedes comparar hasta 3 productos', 'warning');
        return;
    }

    if (!compareList.includes(productId)) {
        compareList.push(productId);
        showNotification(`Producto agregado a comparación (${compareList.length}/3)`, 'success');
        updateCompareButton();
    }
}

function removeFromCompare(productId) {
    compareList = compareList.filter(id => id !== productId);
    updateCompareButton();
    showNotification('Producto removido de comparación', 'info');
}

function updateCompareButton() {
    const compareBtn = document.getElementById('compare-btn');
    if (compareBtn) {
        compareBtn.textContent = `Comparar (${compareList.length})`;
        compareBtn.disabled = compareList.length < 2;
    }
}

function showComparison() {
    if (compareList.length < 2) {
        showNotification('Selecciona al menos 2 productos para comparar', 'warning');
        return;
    }

    // Aquí se implementaría la lógica de comparación
    showNotification('Función de comparación en desarrollo', 'info');
}

// Exportar funciones para uso global
window.showProductDetails = showProductDetails;
window.addToCart = addToCart;
window.requestQuote = requestQuote;
window.searchProducts = searchProducts;
window.sortProducts = sortProducts;
window.shareProduct = shareProduct;
window.addToCompare = addToCompare;
window.removeFromCompare = removeFromCompare;
window.showComparison = showComparison;

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeLazyLoading);
} else {
    initializeLazyLoading();
}