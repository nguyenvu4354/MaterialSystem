<style>
    #sidebarMenu {
        background: #f8f9fa;
        border-right: 1px solid #dee2e6;
        padding: 10px 0;
    }
    #sidebarMenu .nav-link {
        padding: 12px 20px;
        border-radius: 8px;
        margin: 0 10px;
        color: #333;
        font-weight: 500;
        transition: all 0.3s ease;
    }
    #sidebarMenu .nav-link:hover {
        background-color: #8B4513;
        color: #ffffff !important;
        transform: scale(1.02);
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }
    #sidebarMenu .nav-link.active {
        background-color: #8B4513;
        color: #ffffff !important;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }
    #sidebarMenu .nav-link i {
        transition: color 0.3s ease;
    }
    #sidebarMenu .nav-link:hover i,
    #sidebarMenu .nav-link.active i {
        color: #ffffff;
    }
    #sidebarMenu .sidebar-heading {
        padding: 12px 20px;
        margin: 0 10px;
        color: #333;
        font-weight: 600;
        font-size: 1.1rem;
        text-transform: uppercase;
    }
</style>

<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
    <div class="position-sticky pt-4">
        <ul class="nav flex-column menu-list list-unstyled">
            <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                <div class="sidebar-heading mt-4">Create</div>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/CreateExportRequest">
                            <i class="fas fa-plus-square fs-4 me-3"></i>
                            New Export Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/CreateRepairRequest">
                            <i class="fas fa-tools fs-4 me-3"></i>
                            New Repair Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/CreatePurchaseRequest">
                            <i class="fas fa-cart-plus fs-4 me-3"></i>
                            New Purchase Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/CreatePurchaseOrder">
                            <i class="fas fa-file-import fs-4 me-3"></i>
                            New Purchase Order
                        </a>
                    </li>
                </c:if>
            </c:if>
        </ul>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('#sidebarMenu .nav-link');

        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (currentPath.includes(href.split('/').pop())) {
                link.classList.add('active');
                link.setAttribute('aria-current', 'page');
            } else {
                link.classList.remove('active');
                link.removeAttribute('aria-current');
            }
        });

        navLinks.forEach(link => {
            link.addEventListener('click', function (event) {
                navLinks.forEach(l => {
                    l.classList.remove('active');
                    l.removeAttribute('aria-current');
                });

                this.classList.add('active');
                this.setAttribute('aria-current', 'page');
            });
        });
    });
</script>