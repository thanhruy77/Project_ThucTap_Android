package com.example.project_thuctap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference tkdatabase =  database.getReference("login/tk");
    private DatabaseReference mkdatabase = database.getReference("login/mk");
    private ProgressDialog loginning;
    private Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText tk = findViewById(R.id.tk);
        EditText mk = findViewById(R.id.mk);
        TextView qmk = findViewById(R.id.qmk);
        Button login = findViewById(R.id.dangnhap);
        loginning = new ProgressDialog(this);


        // su ly dang nhap

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valuetk = tk.getText().toString().trim();
                String valuemk = mk.getText().toString().trim();
                tkdatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String tk =snapshot.getValue(String.class);
                        if(tk != null && tk.equals(valuetk)){
                            mkdatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String mk = snapshot.getValue(String.class);
                                    if(mk != null && mk.equals(valuemk)){
                                        loginning.setMessage("Đang Đăng Nhập");
                                        loginning.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginning.dismiss();
                                            }
                                        }, 500);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent i = new Intent(Login.this, MapsActivity.class);
                                                startActivity(i);
//                                                finishAffinity();
                                            }
                                        }, 150);
                                    } else {
                                        loginning.setMessage("Đang Đăng Nhập");
                                        loginning.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginning.dismiss();
                                            }
                                        }, 1000);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Login.this,
                                                        "Tài Khoản hoặc Mật khẩu sai!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }, 1000);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });





    }
}