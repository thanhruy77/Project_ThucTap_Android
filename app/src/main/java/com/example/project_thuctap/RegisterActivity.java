package com.example.project_thuctap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference emaildatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        EditText inputName = findViewById(R.id.inputName);
        EditText inputDate = findViewById(R.id.inputDate);
        TextView IntentLogin = findViewById(R.id.alreadyHaveAccount);
        Button Register = findViewById(R.id.Register);

        // đăng ký tài khoản admin
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailValue = inputEmail.getText().toString().trim().toLowerCase();
                String PasswordValue = inputPassword.getText().toString().toLowerCase();
                String NameValue = inputName.getText().toString().toLowerCase();
                String DateValue = inputDate.getText().toString().toLowerCase();
                if(!EmailValue.isEmpty() && !PasswordValue.isEmpty() && !NameValue.isEmpty() && !DateValue.isEmpty()){
                    String formatEmail = EmailValue.replaceAll("[.@\\s]+", "").toLowerCase();
                    emaildatabase = database.getReference("admin/");

                    emaildatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean emailExists = false; // Biến kiểm tra xem email đã tồn tại hay chưa

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                                    String EmailCheck = adminSnapshot.getKey();
                                    assert EmailCheck != null;
                                    if (EmailCheck.equals(formatEmail)) {
                                        emailExists = true;
                                        Toast.makeText(RegisterActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                                        break; // Thoát khỏi vòng lặp khi tìm thấy email tồn tại
                                    }
                                }
                            }
                            if (!emailExists) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!!!", Toast.LENGTH_SHORT).show();
                                emaildatabase.child(formatEmail).child("email").setValue(EmailValue);
                                emaildatabase.child(formatEmail).child("password").setValue(PasswordValue);
                                emaildatabase.child(formatEmail).child("name").setValue(NameValue);
                                emaildatabase.child(formatEmail).child("phone").setValue(DateValue);

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                        }
                    });
                }
            }
        });


        // chuyển sang đăng nhập
        IntentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, Login.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }
}