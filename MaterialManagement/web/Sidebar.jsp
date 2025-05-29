<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse" id="sidebarMenu">
  <div class="position-sticky pt-4">
    <ul class="nav flex-column menu-list list-unstyled">
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="AdminDashboard.jsp">
          <iconify-icon icon="ic:baseline-dashboard" class="fs-4 me-3"></iconify-icon>
          Dashboard
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="UserList">
          <iconify-icon icon="ic:baseline-people" class="fs-4 me-3"></iconify-icon>
          Users
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/dashboardmaterial">
          <iconify-icon icon="ic:baseline-shopping-cart" class="fs-4 me-3"></iconify-icon>
          Material
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="orders.jsp">
          <iconify-icon icon="ic:baseline-receipt" class="fs-4 me-3"></iconify-icon>
          Category
        </a>
      </li>
      <li class="nav-item mb-2">
        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/SupplierServlet">
          <iconify-icon icon="ic:baseline-receipt" class="fs-4 me-3"></iconify-icon>
          Supplier
        </a>
      </li>
    </ul>
  </div>
</div>

<style>
  #sidebarMenu {
    background: #f8f9fa; /* Nền sáng nhẹ từ template */
    border-right: 1px solid #dee2e6; /* Viền phải tinh tế */
    padding: 10px 0;
  }
  #sidebarMenu .nav-link {
    padding: 12px 20px; /* Tăng padding cho dễ nhìn */
    border-radius: 8px; /* Viền tròn nhẹ */
    margin: 0 10px; /* Khoảng cách hai bên */
    color: #333; /* Màu chữ tối, hài hòa với template */
    font-weight: 500; /* Font Montserrat medium */
    transition: all 0.3s ease; /* Chuyển mượt cho tất cả thuộc tính */
  }
  #sidebarMenu .nav-link:hover {
    background-color: #8B4513; /* Màu nâu đậm khi hover */
    color: #ffffff !important; /* Màu chữ trắng */
    transform: scale(1.02); /* Phóng to nhẹ khi hover */
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* Bóng nhẹ */
  }
  #sidebarMenu .nav-link.active {
    background-color: #8B4513; /* Màu nâu đậm cho trang hiện tại */
    color: #ffffff !important; /* Màu chữ trắng */
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); /* Bóng nhẹ */
  }
  #sidebarMenu .nav-link iconify-icon {
    transition: color 0.3s ease; /* Chuyển màu icon mượt */
  }
  #sidebarMenu .nav-link:hover iconify-icon,
  #sidebarMenu .nav-link.active iconify-icon {
    color: #ffffff; /* Màu icon trắng khi hover hoặc active */
  }
</style>

<script>
  document.addEventListener("DOMContentLoaded", function() {
    // Lấy URL hiện tại (chỉ lấy phần tên file, ví dụ: "AdminDashboard.jsp")
    const currentPage = window.location.pathname.split('/').pop();

    // Lấy tất cả các liên kết trong sidebar
    const navLinks = document.querySelectorAll('#sidebarMenu .nav-link');

    // Đánh dấu trang hiện tại
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

    // Xử lý sự kiện click
    navLinks.forEach(link => {
      link.addEventListener('click', function(event) {
        // Xóa lớp active khỏi tất cả các liên kết
        navLinks.forEach(l => {
          l.classList.remove('active');
          l.removeAttribute('aria-current');
        });

        // Thêm lớp active cho liên kết được click
        this.classList.add('active');
        this.setAttribute('aria-current', 'page');
      });
    });
  });
</script>