<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>
    <div class="container py-2">
        <div class="row py-4 pb-0 pb-sm-4 align-items-center">
            <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                <a href="HomePage.jsp">
                    <img src="images/AdminLogo.png" alt="logo" class="img-fluid" width="300px">
                </a>
            </div>
            <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
                <div class="text-end d-none d-xl-block">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <span class="fs-6 text-muted">${sessionScope.user.fullName}</span>
                            <h5 class="mb-0">${sessionScope.user.email}</h5>
                        </c:when>
                        <c:otherwise>
                            <span class="fs-6 text-muted">Guest</span>
                            <h5 class="mb-0">guest@example.com</h5>
                        </c:otherwise>
                    </c:choose>
                </div>
                <a href="logout" class="btn btn-outline-dark btn-lg ms-4">
                    Logout
                </a>
            </div>
        </div>
    </div>
</header>
