<style>
    #sidebarMenu {
        background-color: #343a40;
        padding: 20px 0;
        min-height: 100vh;
    }

    #sidebarMenu .nav-link {
        color: #ffffff;
        padding: 14px 24px;
        border-left: 4px solid transparent;
        font-weight: 500;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
    }

    #sidebarMenu .nav-link i {
        margin-right: 12px;
        font-size: 18px;
        transition: transform 0.3s ease;
    }

    #sidebarMenu .nav-link:hover {
        background-color: #8B4513;
        border-left: 4px solid #ffffff;
        transform: scale(1.02);
    }

    #sidebarMenu .nav-link:hover i {
        transform: scale(1.2);
    }

    #sidebarMenu .nav-link.active {
        background-color: #8B4513;
        border-left: 4px solid #ffffff;
        color: #fff !important;
    }

    .sidebar-heading {
        color: #ccc;
        padding: 10px 24px;
        font-size: 13px;
        text-transform: uppercase;
    }
</style>

<div class="col-md-3 col-lg-2 d-md-block sidebar collapse p-0" id="sidebarMenu">
    <div class="position-sticky pt-3">
        <ul class="nav flex-column list-unstyled">
            <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                <div class="sidebar-heading mt-4">Create</div>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST')}">
                    <li>
                        <a class="nav-link" href="${pageContext.request.contextPath}/CreateExportRequest">
                            <i class="fas fa-plus-square"></i> New Export Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')}">
                    <li>
                        <a class="nav-link" href="${pageContext.request.contextPath}/CreateRepairRequest">
                            <i class="fas fa-tools"></i> New Repair Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST')}">
                    <li>
                        <a class="nav-link" href="${pageContext.request.contextPath}/CreatePurchaseRequest">
                            <i class="fas fa-cart-plus"></i> New Purchase Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                    <li>
                        <a class="nav-link" href="${pageContext.request.contextPath}/CreatePurchaseOrder">
                            <i class="fas fa-file-import"></i> New Purchase Order
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
            link.addEventListener('click', function () {
                navLinks.forEach(l => l.classList.remove('active'));
                this.classList.add('active');
            });
        });
    });
</script>
