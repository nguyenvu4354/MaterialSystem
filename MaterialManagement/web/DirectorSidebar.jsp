<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
  <div class="position-sticky pt-4">
    <ul class="nav flex-column menu-list list-unstyled">
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/Home">
          <i class="fas fa-home fs-4 me-3"></i>
          Home
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/ExportRequestList">
          <i class="fas fa-file-export fs-4 me-3"></i>
          Export Requests
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/ImportRequestList">
          <i class="fas fa-file-import fs-4 me-3"></i>
          Import Requests
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/PurchaseRequestList">
          <i class="fas fa-shopping-cart fs-4 me-3"></i>
          Purchase Requests
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/RequestMaterialList">
          <i class="fas fa-clipboard-list fs-4 me-3"></i>
          Request Material List
        </a>
      </li>
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
  document.addEventListener("DOMContentLoaded", function() {
    // Get the current URL path (e.g., /ExportRequestList)
    const currentPath = window.location.pathname;

    // Get all links in the sidebar
    const navLinks = document.querySelectorAll('#sidebarMenu .nav-link');

    // Mark the current page
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

    // Handle click events
    navLinks.forEach(link => {
      link.addEventListener('click', function(event) {
        // Remove the active class from all links
        navLinks.forEach(l => {
          l.classList.remove('active');
          l.removeAttribute('aria-current');
        });

        // Add the active class to the clicked link
        this.classList.add('active');
        this.setAttribute('aria-current', 'page');
      });
    });
  });
</script>