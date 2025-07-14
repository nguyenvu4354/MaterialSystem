<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.User" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% 
    HttpSession ses = request.getSession(false); 
    User user = (User) ses.getAttribute("user");
    if (user != null) {
        try {
            PermissionDAO permissionDAO = new PermissionDAO();
            List<String> permissionNames = permissionDAO.getPermissionsByRole(user.getRoleId())
                .stream()
                .map(permission -> permission.getPermissionName())
                .collect(Collectors.toList());
            ses.setAttribute("userPermissions", permissionNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>

<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
    <div class="position-sticky pt-4">
        <ul class="nav flex-column menu-list list-unstyled">
            <c:if test="${not empty sessionScope.user}">
                <c:if test="${sessionScope.user.roleId == 1}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/RolePermission">
                            <i class="fas fa-key fs-4 me-3"></i>
                            Permission
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_USER')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/UserList">
                            <i class="fas fa-users fs-4 me-3"></i>
                            Users
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.user.roleId == 1}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/PasswordResetRequests">
                            <i class="fas fa-lock fs-4 me-3"></i>
                            Password Reset Requests
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_DEPARTMENT')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/depairmentlist">
                            <i class="fas fa-building fs-4 me-3"></i>
                            Department
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_UNIT')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/UnitList">
                            <i class="fas fa-cubes fs-4 me-3"></i>
                            Unit
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_INVENTORY')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/StaticInventory">
                            <i class="fas fa-tachometer-alt fs-4 me-3"></i>
                            Inventory Report
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_MATERIAL')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/dashboardmaterial">
                            <i class="fas fa-shopping-cart fs-4 me-3"></i>
                            Material
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_CATEGORY')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/Category">
                            <i class="fas fa-list fs-4 me-3"></i>
                            Category
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_SUPPLIER')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/Supplier">
                            <i class="fas fa-truck fs-4 me-3"></i>
                            Supplier
                        </a>
                    </li>
                </c:if>
            </c:if>
        </ul>
    </div>
</div>

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
</style>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const currentPage = window.location.pathname.split('/').pop();

        const navLinks = document.querySelectorAll('#sidebarMenu .nav-link');

        navLinks.forEach(link => {
            const href = link.getAttribute('href').split('/').pop();
            if (href === currentPage) {
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