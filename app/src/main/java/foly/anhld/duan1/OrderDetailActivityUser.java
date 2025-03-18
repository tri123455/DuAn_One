package foly.anhld.duan1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import android.util.Log;

import java.util.List;
import java.util.Map;

public class OrderDetailActivityUser extends AppCompatActivity {
    Button btnCancelOrder;
    private TextView orderStatus;
    private TextView orderTotal;
    private FirebaseFirestore db;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang_user);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        orderStatus = findViewById(R.id.tvOrderStatususer);
        orderTotal = findViewById(R.id.tvFinalPrice);  // Corrected the reference here
        ivBack = findViewById(R.id.ivBack);

        // Load order details
        String orderId = getIntent().getStringExtra("ORDER_ID"); // Get the order ID from the intent
        if (orderId != null) {
            loadOrderDetails(orderId);  // If orderId is not null, load the order details
        } else {
            Log.e("OrderDetailActivity", "Order ID is null.");
        }

        ivBack.setOnClickListener(view -> {
            // When back button is clicked, go back to OrderActivity
            Intent intent = new Intent(OrderDetailActivityUser.this, OrderActivity.class);
            startActivity(intent);
            finish();
        });
        btnCancelOrder.setOnClickListener(view -> {
            String currentStatus = orderStatus.getText().toString();
            if ("Pending".equalsIgnoreCase(currentStatus)) {
                cancelOrder(orderId);
            } else if ("Transit".equalsIgnoreCase(currentStatus)) {
                showCannotCancelDialog();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Không thể hủy đơn hàng")
                        .setMessage("Trạng thái đơn hàng không hợp lệ để hủy.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

    }

    private void cancelOrder(String orderId) {
        // Xóa đơn hàng khỏi Firebase
        db.collection("orders")
                .document(orderId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("OrderDetailActivity", "Order successfully deleted.");
                    // Sau khi xóa thành công, quay lại màn hình đơn hàng
                    Intent intent = new Intent(OrderDetailActivityUser.this, OrderActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e("OrderDetailActivity", "Error deleting order.", e));
    }
    private void showCannotCancelDialog() {
        // Hiển thị thông báo không cho hủy khi trạng thái là Transit
        new AlertDialog.Builder(this)
                .setTitle("Không thể hủy đơn hàng")
                .setMessage("Đơn hàng đang trong trạng thái Transit và không thể hủy.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void loadOrderDetails(String orderId) {
        db.collection("orders")
                .document(orderId) // Get the document corresponding to the orderId
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Log order data for debugging
                        Log.d("OrderDetailActivity", "Order document found.");

                        // Retrieve order details from Firestore
                        String status = documentSnapshot.getString("status");
                        Double total = documentSnapshot.getDouble("total");
                        String orderDate = documentSnapshot.getString("orderDate");
                        List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("products");

                        // Log the retrieved values for debugging
                        Log.d("OrderDetailActivity", "Order Status: " + status);
                        Log.d("OrderDetailActivity", "Total Price: " + total);
                        Log.d("OrderDetailActivity", "Order Date: " + orderDate);
                        Log.d("OrderDetailActivity", "Number of Products: " + (products != null ? products.size() : 0));

                        // Set the order status
                        orderStatus.setText(status != null ? status : "Status unavailable");

                        // Set the total price
                        orderTotal.setText(total != null ? String.format("Total: %.2f", total) : "Total unavailable");

                        // Set order date
                        TextView orderDateTextView = findViewById(R.id.tvOrderDateuser);
                        orderDateTextView.setText(orderDate != null ? "Order Date: " + orderDate : "Order Date unavailable");

                        // Display products
                        RelativeLayout productsLayout = findViewById(R.id.productsLayout);
                        productsLayout.removeAllViews(); // Clear any old products first

                        if (products != null) {
                            // Loop through products list and display each product in the layout
                            for (Map<String, Object> product : products) {
                                String productName = (String) product.get("productName");
                                Long quantity = (Long) product.get("quantity");
                                Double price = (Double) product.get("price");
                                String size = (String) product.get("size");
                                String productId = (String) product.get("productId");
                                String imageUrl = (String) product.get("imageUrl");

                                // Log product details for debugging
                                Log.d("OrderDetailActivity", "Product: " + productName);
                                Log.d("OrderDetailActivity", "Quantity: " + quantity);
                                Log.d("OrderDetailActivity", "Price: " + price);
                                Log.d("OrderDetailActivity", "Size: " + size);
                                Log.d("OrderDetailActivity", "Product ID: " + productId);
                                Log.d("OrderDetailActivity", "Image URL: " + imageUrl);

                                // Inflate a layout for each product (you can use a custom layout for products)
                                View productView = LayoutInflater.from(this).inflate(R.layout.item_product_order_user, productsLayout, false);

                                // Set product details
                                TextView productNameView = productView.findViewById(R.id.tvProductNameuser);
                                TextView productQuantityView = productView.findViewById(R.id.tvsoluonguser);
                                TextView productPriceView = productView.findViewById(R.id.tvPriceuser);
                                ImageView productImageView = productView.findViewById(R.id.ivproductImageuser);

                                productNameView.setText(productName);
                                productQuantityView.setText("Quantity: " + quantity);
                                productPriceView.setText("Price: " + price);

                                // Use Picasso to load image
                                Picasso.get().load(imageUrl).into(productImageView);

                                // Add product view to the products layout
                                productsLayout.addView(productView);
                            }
                        } else {
                            Log.e("OrderDetailActivity", "No products found in this order.");
                        }
                    } else {
                        Log.e("OrderDetailActivity", "Order document not found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("OrderDetailActivity", "Error fetching order details.", e));
    }

}
