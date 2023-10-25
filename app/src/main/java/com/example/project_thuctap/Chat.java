package com.example.project_thuctap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Chat extends AppCompatActivity {

    String emailnguoinhan;
    String   email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
         emailnguoinhan = getIntent().getStringExtra("emailnguoinhan");
         email = getIntent().getStringExtra("email");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ngu");
        databaseReference.child("ngu").setValue(emailnguoinhan);
        databaseReference.child("ngu1").setValue(email);
    }





}