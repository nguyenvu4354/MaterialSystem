// Department list
public class Department {
    private int departmentId;
    private String departmentName;
    private String description;
    private boolean disable;

    public Department() {}

    public Department(int departmentId, String departmentName, String description, boolean disable) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.description = description;
        this.disable = disable;
    }

    // Getters & Setters
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDisable() { return disable; }
    public void setDisable(boolean disable) { this.disable = disable; }
}

// Create Department
public class Department {
    private int departmentId;
    private String departmentName;
    private String description;

    // Constructors
    public Department(String departmentName, String description) {
        this.departmentName = departmentName;
        this.description = description;
    }

    // Getters and Setters
    public String getDepartmentName() { return departmentName; }
    public String getDescription() { return description; }
}

// Edit Department
public class Department {
    private int departmentId;
    private String departmentName;
    private String description;
    private String status;

    // Getters and Setters
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


