package foly.anhld.duan1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    private TextView txtLogin, txtUp, txtForgot;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Email cố định của người bán datvvph51665@gmail.com
    private final String SELLER_EMAIL = "trily005@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Khai báo các phần tử UI
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        txtLogin = findViewById(R.id.txtLogin);
        txtUp = findViewById(R.id.txtUp);
        txtForgot = findViewById(R.id.txtForgot);

        // Sự kiện đăng ký (Chuyển sang màn hình đăng ký)
        txtUp.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });

        // Sự kiện quên mật khẩu (Chuyển sang màn hình quên mật khẩu)
        txtForgot.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPassword.class));
        });

        // Xử lý sự kiện đăng nhập
        txtLogin.setOnClickListener(v -> {
            loginUser();
        });
    }

    // Kiểm tra đăng nhập
    private void loginUser() {
        String username = usernameTextInputLayout.getEditText().getText().toString().trim();
        String password = passwordTextInputLayout.getEditText().getText().toString().trim();

        // Kiểm tra dữ liệu nhập vào có trống không
        if (TextUtils.isEmpty(username)) {
            usernameTextInputLayout.setError("Tên Email đăng nhập là bắt buộc");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordTextInputLayout.setError("Mật khẩu là bắt buộc");
            return;
        }

        // Tiến hành đăng nhập với Firebase Authentication
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (username.equals(SELLER_EMAIL)) {
                                // Tài khoản người bán
                                Toast.makeText(Login.this, "Đăng nhập người bán thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, QuanLySanPhamAdmin.class));
                            } else {
                                // Tài khoản người mua
                                Toast.makeText(Login.this, "Đăng nhập người mua thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Home.class));
                            }
                            finish();
                        }
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(Login.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
