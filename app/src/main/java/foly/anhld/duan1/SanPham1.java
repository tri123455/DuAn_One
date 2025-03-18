package foly.anhld.duan1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import foly.anhld.duan1.Modol.CartItem;
import foly.anhld.duan1.Modol.SanPham;
import foly.anhld.duan1.Modol.SizeWithQuantity;
import foly.anhld.duan1.adater.SanPham1Adapter;

public class SanPham1 extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private String productId;
    private SizeWithQuantity selectedSize;  // Biến lưu đối tượng kích thước sản phẩm đã chọn

    private RecyclerView sizeRecyclerView;
    private TextView productName, productPrice, productDescription, productIdTextView;
    private ImageView imageView,imgBack;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pham1);

        initViews();

        firestore = FirebaseFirestore.getInstance();
        productId = getIntent().getStringExtra("product_id");
        imgBack = findViewById(R.id.imgBack);

        if (productId != null) {
            fetchProductDetails(productId);
        } else {
            Toast.makeText(SanPham1.this, "Product ID is missing", Toast.LENGTH_SHORT).show();
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SanPham1.this, Home.class));
                finish();
            }
        });
    }

    private void initViews() {
        productName = findViewById(R.id.name_product);
        productPrice = findViewById(R.id.gia_product);
        productDescription = findViewById(R.id.mota_product);
        productIdTextView = findViewById(R.id.tvid1);
        imageView = findViewById(R.id.img_product);
        addToCartButton = findViewById(R.id.btn_add_to_cart);

        sizeRecyclerView = findViewById(R.id.rcvsize);
        sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        addToCartButton.setOnClickListener(v -> {
            // Kiểm tra nếu người dùng chưa chọn kích thước
            if (selectedSize == null) {
                Toast.makeText(SanPham1.this, "Vui lòng chọn kích thước", Toast.LENGTH_SHORT).show();
            } else {
                String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
                if (userId != null) {
                    addToCart(userId, productId, selectedSize);
                } else {
                    Toast.makeText(SanPham1.this, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchProductDetails(String productId) {
        firestore.collection("sanpham").document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            SanPham product = document.toObject(SanPham.class);
                            if (product != null) {
                                displayProductDetails(product);
                            }
                        } else {
                            Toast.makeText(SanPham1.this, "Product not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SanPham1.this, "Failed to load product data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayProductDetails(SanPham product) {
        List<SizeWithQuantity> sizes = product.getSizes();
        if (sizes == null || sizes.isEmpty()) {
            Log.d("SanPham1", "Sizes is empty or null");
        } else {
            Log.d("SanPham1", "Sizes: " + sizes.toString());
        }

        productName.setText(product.getTenSanPham());
        productPrice.setText("Giá: " + (int) product.getGia() + " VND");
        productDescription.setText("Đặc điểm nổi bật: " + product.getMoTa());
        productIdTextView.setText("Product ID: " + productId);

        String imageUrl = product.getHinhAnh();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imageView);
        } else {
            Picasso.get().load(R.drawable.ic_placeholder).into(imageView);
        }

        SanPham1Adapter adapter = new SanPham1Adapter(sizes, size -> {
            selectedSize = size;
            Toast.makeText(SanPham1.this, "Size đã chọn: " + selectedSize.getSize(), Toast.LENGTH_SHORT).show();
        });
        sizeRecyclerView.setAdapter(adapter);
    }

    private void addToCart(String userId, String productId, SizeWithQuantity selectedSize) {
        // Kiểm tra nếu đã chọn kích thước
        if (selectedSize == null) {
            Toast.makeText(SanPham1.this, "Vui lòng chọn kích thước", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedSizeStr = selectedSize.getSize();
        firestore.collection("sanpham").document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            SanPham product = document.toObject(SanPham.class);

                            if (product != null) {
                                // Get the product details to store in the cart
                                String productName = product.getTenSanPham();
                                double productPrice = product.getGia(); // Assuming price is in double
                                String productImage = product.getHinhAnh();

                                // Check if the product is already in the cart
                                firestore.collection("giohang").document(userId)
                                        .collection("sanpham").document(productId + "_" + selectedSizeStr)
                                        .get()
                                        .addOnCompleteListener(cartTask -> {
                                            if (cartTask.isSuccessful() && cartTask.getResult() != null) {
                                                DocumentSnapshot cartDocument = cartTask.getResult();
                                                if (cartDocument.exists()) {
                                                    // Update the existing cart item
                                                    int currentQuantity = cartDocument.getLong("quantity").intValue();
                                                    firestore.collection("giohang").document(userId)
                                                            .collection("sanpham").document(productId + "_" + selectedSizeStr)
                                                            .update("quantity", currentQuantity + 1)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(SanPham1.this, "Đã thêm 1 sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(SanPham1.this, CartActivity.class));
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(SanPham1.this, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    // Create a new cart item with all product details
                                                    CartItem newCartItem = new CartItem(productId, selectedSizeStr, 1, selectedSize.getQuantity(), productName, productPrice, productImage);
                                                    firestore.collection("giohang").document(userId)
                                                            .collection("sanpham").document(productId + "_" + selectedSizeStr)
                                                            .set(newCartItem)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(SanPham1.this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(SanPham1.this, CartActivity.class));
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(SanPham1.this, "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                                                            });
                                                }
                                            } else {
                                                Toast.makeText(SanPham1.this, "Không thể truy cập giỏ hàng!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SanPham1.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SanPham1.this, "Không thể truy cập sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
