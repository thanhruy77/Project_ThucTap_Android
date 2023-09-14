package com.example.project_thuctap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_thuctap.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class Login extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference emaildatabase;
    private DatabaseReference mkdatabase;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        EditText tk = findViewById(R.id.tk);
        EditText mk = findViewById(R.id.mk);
        Button login = findViewById(R.id.dangnhap);
        TextView singup = findViewById(R.id.singup);

        progressBar = findViewById(R.id.progressBar);

        // Kiểm tra nếu đã lưu thông tin đăng nhập trước đó
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");

        if (!savedEmail.isEmpty()) {
            // Nếu đã lưu thông tin đăng nhập, tự động đăng nhập và chuyển đến MainActivity
            progressBar.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            autoLogin(savedEmail);
        }

        // xử lý đăng nhập
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueEmail = tk.getText().toString().trim().toLowerCase();
                String valuemk = mk.getText().toString().trim();
                if (!valueEmail.isEmpty() && !valuemk.isEmpty()) {
                    String formatValueEmail = valueEmail.replaceAll("[.@\\s]+", "").toLowerCase();
                    emaildatabase = database.getReference("admin/" + formatValueEmail + "/email");

                    // Ẩn nút đăng nhập và hiển thị ProgressBar
                    login.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    emaildatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tk = snapshot.getValue(String.class);
                            if (tk != null && tk.equals(valueEmail)) {
                                mkdatabase = database.getReference("admin/" + formatValueEmail + "/password");
                                mkdatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String mk = Objects.requireNonNull(snapshot.getValue()).toString();
                                        if (mk.equals(valuemk)) {
                                            // Lưu thông tin đăng nhập sau khi đăng nhập thành công
                                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email", formatValueEmail);
                                            editor.apply();

                                            // Ẩn ProgressBar và chuyển đến MainActivity
                                            progressBar.setVisibility(View.GONE);
                                            Intent i = new Intent(Login.this, MainActivity.class);
                                            i.putExtra("email", formatValueEmail);
                                            startActivity(i);
                                            login.setVisibility(View.VISIBLE);
                                            finishAffinity();
                                        } else {
                                            // Ẩn ProgressBar, hiển thị nút đăng nhập và thông báo lỗi
                                            progressBar.setVisibility(View.GONE);
                                            login.setVisibility(View.VISIBLE);
                                            Toast.makeText(Login.this,
                                                    "Tài Khoản hoặc Mật khẩu sai!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            } else {
                                // Email không tồn tại
                                progressBar.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);
                                Toast.makeText(Login.this,
                                        "Email không tồn tại!!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    // Ẩn ProgressBar, hiển thị nút đăng nhập và thông báo lỗi
                    progressBar.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this,
                            "Không được để trống!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // intent sang Register
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegisterActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }

    private void autoLogin(String savedEmail) {
        // Tự động đăng nhập bằng email đã lưu
        // Thực hiện các thao tác đăng nhập tại đây
        // Sau khi đăng nhập thành công, chuyển đến MainActivity

        progressBar.setVisibility(View.VISIBLE);
        emaildatabase = database.getReference("admin/" + savedEmail + "/email");
        emaildatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Đã đăng nhập trước đó, tiến hành đăng nhập và chuyển đến MainActivity
                progressBar.setVisibility(View.GONE);
                Intent i = new Intent(Login.this, MainActivity.class);
                i.putExtra("email", savedEmail);
                startActivity(i);
                finish(); // Đóng trang đăng nhập
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
