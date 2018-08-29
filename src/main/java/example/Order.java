package example;

public class Order {
    private int employeeId;
    private String productName;

    public Order(int employeeId, String productName) {
        this.employeeId = employeeId;
        this.productName = productName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
