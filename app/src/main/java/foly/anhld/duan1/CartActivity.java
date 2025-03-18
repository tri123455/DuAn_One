package foly.anhld.duan1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foly.anhld.duan1.Modol.CartItem;
import foly.anhld.duan1.Modol.Order;
import foly.anhld.duan1.Modol.SanPham;
import foly.anhld.duan1.adater.CartAdapter;

public class CartActivity extends AppCompatActivity {
    private Button btnBuy;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private String userId;
    private TextView totalPriceTextView; // Reference to the total price TextView
    private List<CartItem> cartItems;
    private ImageView ivBack ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnBuy=findViewById(R.id.btnBuy);
        totalPriceTextView = findViewById(R.id.tvTotal); // Initialize the total price TextView
        ivBack = findViewById(R.id.ivBack);
        firestore = FirebaseFirestore.getInstance();

        // Lấy thông tin userId từ Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Lấy userId của người dùng hiện tại
            loadCartItems(); // Tải dữ liệu giỏ hàng
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu chưa đăng nhập
        }
        btnBuy.setOnClickListener(v -> showConfirmationDialog());
        menuBottom();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String source = getIntent().getStringExtra("source");

                if ("home".equals(source)) {
                    // Nếu nguồn là từ Home, quay lại Home
                    Intent intent = new Intent(CartActivity.this, Home.class);
                    startActivity(intent);
                    finish();
                } else if ("detail".equals(source)) {
                    // Nếu nguồn là từ Chi tiết sản phẩm, quay lại Chi tiết sản phẩm
                    Intent intent = new Intent(CartActivity.this, SanPham1.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Nếu không có nguồn rõ ràng, mặc định xử lý như Back Stack
                    onBackPressed();
                }
            }
        });
    }



    private void menuBottom(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard) {
                // Chuyển đến Activity Home
                startActivity(new Intent( CartActivity.this,  CartActivity.class));
                return true;
            } else if (itemId == R.id.navigation_home) {
                // Chuyển đến Activity Dashboard
                startActivity(new Intent(CartActivity.this, Home.class));
                return true;
            }
//            else if (itemId == R.id.navigation_notificatio) {
//                // Chuyển đến Activity Notification
//                startActivity(new Intent(Home.this,  OrderActivity.class));
//                return true;
//            }
            else if (itemId == R.id.navigation_notificatio) {
                // Giả sử userId đã có sẵn ở Home (bạn cần lấy userId từ FirebaseAuth)
//                String userId = "userId"; // Thay bằng cách lấy userId thực tế
//                Intent intent = new Intent(Home.this, OrderActivity.class);
//                intent.putExtra("userId", userId);
//                startActivity(intent);
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
                return true;
            }

            else if (itemId == R.id.dashboard) {
                // Chuyển đến Activity Login
                startActivity(new Intent(CartActivity.this,  CaNhan.class));
                return true;
            } else {
                return false;
            }
        });
    }


    private void loadCartItems() {
        firestore.collection("giohang").document(userId).collection("sanpham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot result = task.getResult();
                        cartItems = result.toObjects(CartItem.class); // Cập nhật cartItems

                        if (cartItems.isEmpty()) {
                            Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
                        }

                        // Initialize adapter and pass the total price TextView
                        adapter = new CartAdapter(cartItems, this::onCartItemRemove, this::onCartItemQuantityChanged, totalPriceTextView);
                        recyclerView.setAdapter(adapter);

                        // Calculate and display the total price when the cart is loaded
                        updateTotalPrice(cartItems);
                    } else {
                        Log.e("FirebaseError", "Lỗi khi tải giỏ hàng: " + task.getException());
                        Toast.makeText(this, "Không thể tải giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void updateTotalPrice(List<CartItem> cartItems) {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getProductPrice() * item.getQuantity();
        }
        totalPriceTextView.setText("Tổng tiền: " + totalPrice + " VND");
    }

    private void onCartItemRemove(@NonNull CartItem cartItem) {
        // Tạo ID tài liệu duy nhất từ mã sản phẩm và size
        String productIdSize = cartItem.getProductId() + "_" + cartItem.getSize();

        firestore.collection("giohang").document(userId).collection("sanpham")
                .document(productIdSize) // Dùng ID duy nhất là mã sản phẩm và size
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa sản phẩm khỏi giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                    loadCartItems(); // Reload the cart after removal
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Lỗi khi xóa sản phẩm: " + e.getMessage());
                    Toast.makeText(this, "Không thể xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                });
    }

    private void onCartItemQuantityChanged(@NonNull CartItem cartItem, int newQuantity) {
        // Tạo ID tài liệu duy nhất từ mã sản phẩm và size
        String productIdSize = cartItem.getProductId() + "_" + cartItem.getSize();

        firestore.collection("giohang").document(userId).collection("sanpham")
                .document(productIdSize) // Dùng ID duy nhất là mã sản phẩm và size
                .get() // Lấy tài liệu trước khi cập nhật
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Nếu tài liệu tồn tại, thực hiện cập nhật
                        firestore.collection("giohang").document(userId).collection("sanpham")
                                .document(productIdSize) // Dùng ID duy nhất là mã sản phẩm và size
                                .update("quantity", newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Cập nhật số lượng thành công!", Toast.LENGTH_SHORT).show();
                                    loadCartItems(); // Reload cart items to reflect the updated quantity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Không thể cập nhật số lượng!", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Nếu tài liệu không tồn tại, thông báo cho người dùng
                        Toast.makeText(this, "Sản phẩm không tồn tại trong giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể kiểm tra sản phẩm!", Toast.LENGTH_SHORT).show();
                });
    }
    private void showConfirmationDialog() {
        // Create a dialog layout with input fields for address, phone number, and recipient's name
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_name_sdt, null);

        final TextView edtName = dialogView.findViewById(R.id.edtName);
        final TextView edtAddress = dialogView.findViewById(R.id.edtAddress);
        final TextView edtPhoneNumber = dialogView.findViewById(R.id.edtPhoneNumber);

        // Create an AlertDialog to confirm the order with additional fields for name, address, and phone number
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt hàng")
                .setView(dialogView)
                .setPositiveButton("Đặt hàng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the user input
                        String name = edtName.getText().toString().trim();
                        String address = edtAddress.getText().toString().trim();
                        String phoneNumber = edtPhoneNumber.getText().toString().trim();

                        // Check if all fields are filled
                        if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                            Toast.makeText(CartActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Call placeOrder method with the additional information
                            placeOrder(name, address, phoneNumber);
                        }
                    }
                })
                .setNegativeButton("Hủy", null) // Do nothing if canceled
                .show();
    }

    private void placeOrder(String recipientName, String address, String phoneNumber) {
        // Generate a unique order ID
        String orderId = firestore.collection("orders").document().getId();

        // Lấy ngày hiện tại từ thiết bị và định dạng nó
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderDate = sdf.format(new Date()); // Định dạng ngày hiện tại thành chuỗi

        String status = "Pending"; // Initially set the status to "Pending"
        double totalPrice = calculateTotalPrice(); // Calculate total price

        // Tạo đối tượng Order với thông tin người nhận
        Order order = new Order(orderId, userId, cartItems, orderDate, status, totalPrice, recipientName, address, phoneNumber);

        // Lưu thông tin đơn hàng lên Firestore
        firestore.collection("orders")
                .document(orderId) // Sử dụng orderId làm ID tài liệu
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    navigateToOrderActivity(orderId); // Navigate to OrderActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Không thể đặt hàng!", Toast.LENGTH_SHORT).show();
                });
    }

    private double calculateTotalPrice() {
        double totalPrice = 0;
        if (cartItems != null && !cartItems.isEmpty()) {
            for (CartItem item : cartItems) {
                totalPrice += item.getProductPrice() * item.getQuantity();
            }
        } else {
            Log.w("CartActivity", "Giỏ hàng trống hoặc cartItems chưa được tải.");
        }
        return totalPrice;
    }

    private void navigateToOrderActivity(String orderId) {
        // Trong Activity Mua hàng
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("userId", userId);  // Truyền userId qua Intent
        intent.putExtra("orderId", orderId); // Truyền orderId qua Intent
        Log.d("MuaHangActivity", "Chuyển sang màn OrderActivity với userId: " + userId + " và orderId: " + orderId);  // Log khi chuyển màn hình
        startActivity(intent);
    }

}
