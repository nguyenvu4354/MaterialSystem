package entity;

public class Supplier {
    private int supplierId;
    private String supplierName;
    private String contactInfo;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String taxId;

    public Supplier() {
        
    }

    public Supplier(int supplierId, String supplierName, String contactInfo, String address,
                    String phoneNumber, String email, String description, String taxId) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.description = description;
        this.taxId = taxId;
    }

    // Getters và setters
    public int getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public String getContactInfo() {
        return contactInfo;
    }
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTaxId() {
        return taxId;
    }
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", taxId='" + taxId + '\'' +
                '}';
    }
}
