//-- Tạo cơ sở dữ liệu
//CREATE DATABASE IF NOT EXISTS material_management;
//USE material_management;
//
//
//-- Bảng Roles (Quản lý vai trò người dùng)
//CREATE TABLE Roles (
//    role_id INT AUTO_INCREMENT PRIMARY KEY,
//    role_name VARCHAR(50) NOT NULL UNIQUE,
//    description TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0
//);
//
//
//-- Bảng Users (Quản lý thông tin người dùng)
//CREATE TABLE Users (
//    user_id INT AUTO_INCREMENT PRIMARY KEY,
//    username VARCHAR(50) NOT NULL UNIQUE,
//    password VARCHAR(255) NOT NULL,
//    full_name VARCHAR(100) NOT NULL,
//    email VARCHAR(100) NOT NULL,
//    phone_number VARCHAR(20),
//    address VARCHAR(255),
//    user_picture VARCHAR(255),
//    role_id INT NOT NULL,
//    date_of_birth DATE,
//    gender ENUM('male', 'female', 'other'),
//    description TEXT,
//    status ENUM('active', 'inactive', 'deleted') DEFAULT 'active',
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE RESTRICT
//);
//
//
//-- Bảng Categories (Quản lý danh mục vật tư)
//CREATE TABLE Categories (
//    category_id INT AUTO_INCREMENT PRIMARY KEY,
//    category_name VARCHAR(100) NOT NULL,
//    parent_id INT DEFAULT NULL,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    status ENUM('active', 'inactive') DEFAULT 'active',
//    description TEXT,
//    priority INT DEFAULT 1,
//    FOREIGN KEY (parent_id) REFERENCES Categories(category_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Suppliers (Quản lý nhà cung cấp)
//CREATE TABLE Suppliers (
//    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
//    supplier_name VARCHAR(100) NOT NULL,
//    contact_info VARCHAR(255),
//    address TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    phone_number VARCHAR(20),
//    email VARCHAR(100),
//    description TEXT,
//    tax_id VARCHAR(50),
//    disable TINYINT DEFAULT 0
//);
//
//
//-- Bảng Materials (Quản lý vật tư)
//CREATE TABLE Materials (
//    material_id INT AUTO_INCREMENT PRIMARY KEY,
//    material_code VARCHAR(50) NOT NULL UNIQUE,
//    material_name VARCHAR(100) NOT NULL,
//    materials_url TEXT,
//    material_status ENUM('new', 'used', 'damaged') DEFAULT 'new',
//    condition_percentage INT DEFAULT 100 CHECK (condition_percentage BETWEEN 0 AND 100),
//    price DECIMAL(15,2) NOT NULL,
//    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
//    category_id INT NOT NULL,
//    supplier_id INT DEFAULT NULL,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE RESTRICT,
//    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Export_Requests (Quản lý đơn xuất vật tư)
//CREATE TABLE Export_Requests (
//    export_request_id INT AUTO_INCREMENT PRIMARY KEY,
//    request_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
//    status ENUM('draft', 'pending', 'approved', 'rejected', 'completed') DEFAULT 'draft',
//    reason TEXT,
//    approved_by INT DEFAULT NULL,
//    approved_at DATETIME DEFAULT NULL,
//    rejection_reason TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT,
//    FOREIGN KEY (approved_by) REFERENCES Users(user_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Export_Request_Details (Chi tiết đơn xuất vật tư)
//CREATE TABLE Export_Request_Details (
//    detail_id INT AUTO_INCREMENT PRIMARY KEY,
//    export_request_id INT NOT NULL,
//    material_id INT NOT NULL,
//    quantity INT NOT NULL CHECK (quantity > 0),
//    notes TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    FOREIGN KEY (export_request_id) REFERENCES Export_Requests(export_request_id) ON DELETE CASCADE,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE RESTRICT
//);
//
//
//-- Bảng Import_Requests (Quản lý đơn nhập vật tư)
//CREATE TABLE Import_Requests (
//    import_request_id INT AUTO_INCREMENT PRIMARY KEY,
//    request_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
//    status ENUM('draft', 'pending', 'approved', 'rejected', 'completed') DEFAULT 'draft',
//    reason TEXT,
//    approved_by INT DEFAULT NULL,
//    approved_at DATETIME DEFAULT NULL,
//    rejection_reason TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT,
//    FOREIGN KEY (approved_by) REFERENCES Users(user_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Import_Request_Details (Chi tiết đơn nhập vật tư)
//CREATE TABLE Import_Request_Details (
//    detail_id INT AUTO_INCREMENT PRIMARY KEY,
//    import_request_id INT NOT NULL,
//    material_id INT DEFAULT NULL,
//    material_code VARCHAR(50),
//    material_name VARCHAR(100),
//    materials_url TEXT,
//    material_status ENUM('new', 'used', 'damaged') DEFAULT 'new',
//    condition_percentage INT DEFAULT 100 CHECK (condition_percentage BETWEEN 0 AND 100),
//    price DECIMAL(15,2),
//    quantity INT NOT NULL CHECK (quantity > 0),
//    category_id INT DEFAULT NULL,
//    category_name VARCHAR(100),
//    supplier_id INT DEFAULT NULL,
//    supplier_name VARCHAR(100),
//    contact_info VARCHAR(255),
//    address TEXT,
//    phone_number VARCHAR(20),
//    email VARCHAR(100),
//    tax_id VARCHAR(50),
//    notes TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    FOREIGN KEY (import_request_id) REFERENCES Import_Requests(import_request_id) ON DELETE CASCADE,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE RESTRICT,
//    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE SET NULL,
//    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Purchase_Requests (Quản lý đơn đề nghị mua vật tư)
//CREATE TABLE Purchase_Requests (
//    purchase_request_id INT AUTO_INCREMENT PRIMARY KEY,
//    request_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
//    status ENUM('draft', 'pending', 'approved', 'rejected', 'completed') DEFAULT 'draft',
//    reason TEXT,
//    approved_by INT DEFAULT NULL,
//    approved_at DATETIME DEFAULT NULL,
//    rejection_reason TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT,
//    FOREIGN KEY (approved_by) REFERENCES Users(user_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Purchase_Request_Details (Chi tiết đơn đề nghị mua vật tư)
//CREATE TABLE Purchase_Request_Details (
//    detail_id INT AUTO_INCREMENT PRIMARY KEY,
//    purchase_request_id INT NOT NULL,
//    material_name VARCHAR(100) NOT NULL,
//    category_id INT NOT NULL,
//    quantity INT NOT NULL CHECK (quantity > 0),
//    estimated_price DECIMAL(15,2) DEFAULT NULL,
//    notes TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    FOREIGN KEY (purchase_request_id) REFERENCES Purchase_Requests(purchase_request_id) ON DELETE CASCADE,
//    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE RESTRICT
//);
//
//
//-- Bảng Repair_Requests (Quản lý đơn xin sửa chữa vật tư)
//CREATE TABLE Repair_Requests (
//    repair_request_id INT AUTO_INCREMENT PRIMARY KEY,
//    request_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
//    status ENUM('draft', 'pending', 'approved', 'rejected', 'completed') DEFAULT 'draft',
//    reason TEXT,
//    approved_by INT DEFAULT NULL,
//    approved_at DATETIME DEFAULT NULL,
//    rejection_reason TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT,
//    FOREIGN KEY (approved_by) REFERENCES Users(user_id) ON DELETE SET NULL
//);
//
//
//-- Bảng Repair_Request_Details (Chi tiết đơn xin sửa chữa vật tư)
//CREATE TABLE Repair_Request_Details (
//    detail_id INT AUTO_INCREMENT PRIMARY KEY,
//    repair_request_id INT NOT NULL,
//    material_id INT NOT NULL,
//    damage_description TEXT NOT NULL,
//    repair_cost DECIMAL(15,2) DEFAULT NULL,
//    notes TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    FOREIGN KEY (repair_request_id) REFERENCES Repair_Requests(repair_request_id) ON DELETE CASCADE,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE RESTRICT
//);
//
//
