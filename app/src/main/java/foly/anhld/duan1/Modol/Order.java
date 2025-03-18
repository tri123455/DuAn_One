package foly.anhld.duan1.Modol;

import java.util.List;

public class Order {
    private String orderId;        // Mã đơn hàng (Firestore auto-generated)
    private String userId;         // Mã người dùng
    private List<CartItem> cartItems;  // Danh sách sản phẩm trong đơn hàng
    private String orderDate;      // Ngày đặt hàng
    private String status;         // Trạng thái đơn hàng (Pending Approval, In Transit, Delivered)
    private double totalPrice;     // Tổng tiền đơn hàng
    private String recipientName;  // Tên người nhận
    private String address;        // Địa chỉ người nhận
    private String phoneNumber;    // Số điện thoại người nhận

    public Order() {
    }

    public Order(String orderId, String userId, List<CartItem> cartItems, String orderDate, String status, double totalPrice, String recipientName, String address, String phoneNumber) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartItems = cartItems;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.recipientName = recipientName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
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
}
