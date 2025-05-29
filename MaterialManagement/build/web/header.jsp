<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header class="header">
    <div class="header-container">
        <div class="header-left">
            <div class="logo">
                <img src="assets/images/logo.png" alt="Logo">
                <span>Material Management</span>
            </div>
        </div>
        
        <div class="header-right">
            <div class="search-box">
                <input type="text" placeholder="Search...">
                <i class="fas fa-search"></i>
            </div>
            
            <div class="header-user">
                <div class="notifications">
                    <i class="fas fa-bell"></i>
                    <span class="badge">3</span>
                </div>
                
                <div class="user-profile">
                    <img src="assets/images/avatar.jpg" alt="User Avatar">
                    <div class="user-info">
                        <span class="user-name">${sessionScope.user.fullName}</span>
                        <span class="user-role">${sessionScope.user.role}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

<style>
.header {
    background: #ffffff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    padding: 1rem;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
}

.header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1400px;
    margin: 0 auto;
}

.header-left .logo {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.logo img {
    height: 40px;
}

.logo span {
    font-size: 1.2rem;
    font-weight: bold;
    color: #333;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 2rem;
}

.search-box {
    position: relative;
}

.search-box input {
    padding: 0.5rem 1rem;
    padding-right: 2.5rem;
    border: 1px solid #ddd;
    border-radius: 20px;
    width: 250px;
}

.search-box i {
    position: absolute;
    right: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #666;
}

.header-user {
    display: flex;
    align-items: center;
    gap: 1.5rem;
}

.notifications {
    position: relative;
}

.notifications i {
    font-size: 1.2rem;
    color: #666;
}

.badge {
    position: absolute;
    top: -5px;
    right: -5px;
    background: #ff4444;
    color: white;
    border-radius: 50%;
    padding: 2px 6px;
    font-size: 0.7rem;
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.user-profile img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
}

.user-info {
    display: flex;
    flex-direction: column;
}

.user-name {
    font-weight: 600;
    color: #333;
}

.user-role {
    font-size: 0.8rem;
    color: #666;
}
</style> 