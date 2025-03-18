package foly.anhld.duan1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import foly.anhld.duan1.Modol.Order;
import foly.anhld.duan1.adater.OrderAdapter;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore firestore;
    private String userId;

    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        ivBack = findViewById(R.id.ivBack);

        // Get the current userId from FirebaseAuth
        userId = getIntent().getStringExtra("userId");  // Get userId from Intent or FirebaseAuth
        
        if (userId != null) {
            loadOrders(userId);
        } else {
            Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
        }

        // nút chuyển
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String orderId = getIntent().getStringExtra("ORDER_ID");
                String source = intent.getStringExtra("source");
                if ("home".equals(source)) {
                    startActivity(new Intent(OrderActivity.this, Home.class));
                } else if ("cart".equals(source)) {
                    startActivity(new Intent(OrderActivity.this, CartActivity.class));
                }
                else if ("CaNhan".equals(source)) {
                    startActivity(new Intent(OrderActivity.this, CaNhan.class));
                }

                finish();
            }
        });
    }

    private void loadOrders(String userId) {
        Log.d("OrderActivity", "Bắt đầu tải danh sách đơn hàng cho userId: " + userId);  // Log khi bắt đầu tải dữ liệu
        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("OrderActivity", "Truy vấn Firestore thành công.");  // Log khi truy vấn thành công
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            orderList = new ArrayList<>(querySnapshot.toObjects(Order.class));
                            if (orderList.isEmpty()) {
                                Log.d("OrderActivity", "Không có đơn hàng nào.");  // Log khi không có đơn hàng
                                Toast.makeText(this, "Bạn chưa có đơn hàng nào.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("OrderActivity", "Đã tải thành công " + orderList.size() + " đơn hàng.");  // Log số lượng đơn hàng
                                orderAdapter = new OrderAdapter(orderList, this);
                                recyclerView.setAdapter(orderAdapter);
                            }
                        }
                    } else {
                        Log.e("OrderActivity", "Lỗi khi truy vấn Firestore: " + task.getException().getMessage());  // Log khi có lỗi
                        Toast.makeText(this, "Không thể tải danh sách đơn hàng.", Toast.LENGTH_SHORT).show();
                    }
                });


        }

}
