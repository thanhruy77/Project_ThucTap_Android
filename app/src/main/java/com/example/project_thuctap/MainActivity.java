package com.example.project_thuctap;

// MainActivity.java
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private DatabaseReference users;

    String email;

    String emailuser;
    String phoneuser;
    String nameuser;

    private List<DataSnapshot> dataSnapshots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button settinguser = findViewById(R.id.user);


         email = getIntent().getStringExtra("email");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        users = database.getReference("admin/"+email+"/users");

        adapter = new MyAdapter(this, dataSnapshots);
        adapter.setUserEmail(email); // Thiết lập email
        adapter.setEmailAdmin(email); // Thiết lập emailadmin
        recyclerView.setAdapter(adapter);


        Button logout = findViewById(R.id.logout);
        // đang xuất
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Xác nhận đăng xuất");
                builder.setMessage("Bạn có muốn đăng xuất khỏi ứng dụng không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent i = new Intent(MainActivity.this, Login.class);
                        startActivity(i);
                        finishAffinity();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog và không thực hiện đăng xuất
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // hiển thị users
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                dataSnapshots.add(dataSnapshot);
                adapter.notifyItemInserted(dataSnapshots.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Xác định vị trí của dữ liệu đã thay đổi trong danh sách dataSnapshots
                // Ví dụ: tìm key trong dataSnapshots và lấy vị trí của nó trong danh sách
                String keyChanged = dataSnapshot.getKey();
                int position = -1;
                for (int i = 0; i < dataSnapshots.size(); i++) {
                    DataSnapshot snapshot = dataSnapshots.get(i);
                    if (snapshot.getKey().equals(keyChanged)) {
                        position = i;
                        break;
                    }
                }

                if (position != -1) {
                    // Cập nhật dữ liệu của CardView tại vị trí đã tìm thấy
                    dataSnapshots.set(position, dataSnapshot);
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Xử lý khi một key bị xóa
                String keyRemoved = dataSnapshot.getKey();
                int position = -1;
                for (int i = 0; i < dataSnapshots.size(); i++) {
                    DataSnapshot snapshot = dataSnapshots.get(i);
                    if (snapshot.getKey().equals(keyRemoved)) {
                        position = i;
                        break;
                    }
                }

                if (position != -1) {
                    // Loại bỏ dữ liệu tại vị trí đã tìm thấy
                    dataSnapshots.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Xử lý khi một key bị di chuyển (nếu cần)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi (nếu cần)
            }
        });



        // gửi dữ liệu người dùng
        DatabaseReference datauser = FirebaseDatabase.getInstance().getReference().child("admin/"+email);

        datauser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailuser = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        datauser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameuser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        datauser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phoneuser = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // qua kết bạn
        settinguser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Friends.class);
                intent.putExtra("email",email);
                intent.putExtra("emailuser",emailuser);
                intent.putExtra("nameuser",nameuser);
                intent.putExtra("phoneuser",phoneuser);
                startActivity(intent);
            }
        });
    }
}

