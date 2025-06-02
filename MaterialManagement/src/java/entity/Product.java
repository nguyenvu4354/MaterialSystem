/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Nhat Anh
 */
public class Product {

    private int materialId;
    private String materialCode;
    private String materialName;
    private String materialsUrl;
    private double price;
    private String description;
    private double rating;
    private int categoryId;

    public Product() {
    }

    public Product(int materialId, String materialCode, String materialName, String materialsUrl,
            double price, String description, double rating, int categoryId) {
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.materialsUrl = materialsUrl;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.categoryId = categoryId;
    }

    // Getter và Setter
    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialsUrl() {
        return materialsUrl;
    }

    public void setMaterialsUrl(String materialsUrl) {
        this.materialsUrl = materialsUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Product{" + "materialId=" + materialId + ", materialCode=" + materialCode + ", materialName=" + materialName + ", materialsUrl=" + materialsUrl + ", price=" + price + ", description=" + description + ", rating=" + rating + ", categoryId=" + categoryId + '}';
    }
    
}
