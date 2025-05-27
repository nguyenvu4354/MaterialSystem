///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package DataBase;
//
///**
// *
// * @author Admin
// */
//public class DataBase {
//    -- Create database
//CREATE DATABASE IF NOT EXISTS material_management;
//USE material_management;
//
//
//-- Table 1: Roles (User roles)
//CREATE TABLE Roles (
//    role_id INT AUTO_INCREMENT PRIMARY KEY,
//    role_name VARCHAR(50) NOT NULL UNIQUE,
//    description TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0
//);
//
//
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
//    status ENUM('active', 'inactive', 'deleted') DEFAULT 'active', -- Thêm 'deleted'
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    FOREIGN KEY (role_id) REFERENCES Roles(role_id) ON DELETE RESTRICT
//);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//-- Table 3: Categories
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
//
//
//
//
//-- Table 4: Suppliers (New table for managing suppliers)
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
//    warehouse_id INT DEFAULT NULL,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE RESTRICT,
//    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL,
//    FOREIGN KEY (warehouse_id) REFERENCES Warehouse(warehouse_id) ON DELETE SET NULL
//);
//
//
//
//
//CREATE TABLE Warehouse (
//    warehouse_id INT AUTO_INCREMENT PRIMARY KEY,
//    warehouse_name VARCHAR(100) NOT NULL,
//    location VARCHAR(255),
//    capacity INT DEFAULT 0 CHECK (capacity >= 0),
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0
//);
//
//
//
//
//
//
//-- Table 5: Materials (Updated to reference Suppliers table)
//CREATE TABLE Materials (
//    material_id INT AUTO_INCREMENT PRIMARY KEY,
//    material_code VARCHAR(50) NOT NULL UNIQUE,
//    material_name VARCHAR(100) NOT NULL,
//    materials_url VARCHAR(255),
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
//-- Table 6: Import_Notes (Import notes)
//CREATE TABLE Import_Notes (
//    import_id INT AUTO_INCREMENT PRIMARY KEY,
//    import_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    import_date DATETIME NOT NULL,
//    supplier VARCHAR(100),
//    notes TEXT,
//    status ENUM('pending', 'completed') DEFAULT 'pending',
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT
//);
//
//
//-- Table 7: Import_Note_Details (Import note details)
//CREATE TABLE Import_Note_Details (
//    import_detail_id INT AUTO_INCREMENT PRIMARY KEY,
//    import_id INT NOT NULL,
//    material_id INT NOT NULL,
//    quantity INT NOT NULL CHECK (quantity > 0),
//    unit_price DECIMAL(15,2) NOT NULL,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (import_id) REFERENCES Import_Notes(import_id) ON DELETE CASCADE,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE RESTRICT
//);
//
//
//-- Table 8: Export_Notes (Export notes)
//CREATE TABLE Export_Notes (
//    export_id INT AUTO_INCREMENT PRIMARY KEY,
//    export_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    receiver_id INT NOT NULL,
//    export_date DATETIME NOT NULL,
//    notes TEXT,
//    status ENUM('pending', 'completed') DEFAULT 'pending',
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT,
//    FOREIGN KEY (receiver_id) REFERENCES Users(user_id) ON DELETE RESTRICT
//);
//
//
//-- Table 9: Export_Note_Details (Export note details)
//CREATE TABLE Export_Note_Details (
//    export_detail_id INT AUTO_INCREMENT PRIMARY KEY,
//    export_id INT NOT NULL,
//    material_id INT NOT NULL,
//    quantity INT NOT NULL CHECK (quantity > 0),
//    unit_price DECIMAL(15,2),
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (export_id) REFERENCES Export_Notes(export_id) ON DELETE CASCADE,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE RESTRICT
//);
//
//
//-- Table 10: Requests
//CREATE TABLE Requests (
//    request_id INT AUTO_INCREMENT PRIMARY KEY,
//    request_code VARCHAR(50) NOT NULL UNIQUE,
//    user_id INT NOT NULL,
//    request_type ENUM('export', 'purchase', 'repair') NOT NULL,
//    material_id INT DEFAULT NULL,
//    quantity INT CHECK (quantity > 0),
//    reason TEXT,
//    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
//    approver_id INT DEFAULT NULL,
//    approve_reason TEXT,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE RESTRICT,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE SET NULL,
//    FOREIGN KEY (approver_id) REFERENCES Users(user_id) ON DELETE SET NULL
//);
//
//
//-- Table 11: Inventory_Logs (Inventory logs)
//CREATE TABLE Inventory_Logs (
//    log_id INT AUTO_INCREMENT PRIMARY KEY,
//    material_id INT NOT NULL,
//    action ENUM('import', 'export') NOT NULL,
//    quantity INT NOT NULL CHECK (quantity > 0),
//    note_id INT NOT NULL,
//    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
//    disable TINYINT DEFAULT 0,
//    FOREIGN KEY (material_id) REFERENCES Materials(material_id) ON DELETE RESTRICT
//);
//
//
//
//}
