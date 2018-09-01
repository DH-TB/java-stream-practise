package example;

class Order {
    private int employeeId;
    private String productName;

    Order(int employeeId, String productName) {
        this.employeeId = employeeId;
        this.productName = productName;
    }

    int getEmployeeId() {
        return employeeId;
    }

    String getProductName() {
        return productName;
    }
}
