package com.example.project_thuctap;

// MyAdapter.java

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Map<String, String> dataMap = new HashMap<>();
    private List<DataSnapshot> dataSnapshots;
    private Context context;
    public MyAdapter(Context context, List<DataSnapshot> dataSnapshots) {
        this.context = context;
        this.dataSnapshots = dataSnapshots;
    }
    private int selectedPosition = RecyclerView.NO_POSITION; // Đặt giá trị mặc định là NO_POSITION
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView để hiển thị sự thay đổi
    }
    public int getSelectedPosition() {
        return selectedPosition;
    }


    private String userEmail; // Thêm biến để lưu trữ email
    public void setUserEmail(String email) {
        this.userEmail = email;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataSnapshot dataSnapshot = dataSnapshots.get(position);
        String nameValue = String.valueOf(dataSnapshot.child("name").getValue());
        String dateValue = String.valueOf(dataSnapshot.child("date").getValue());
        String passwordValue = String.valueOf(dataSnapshot.child("password").getValue());
        String key = dataSnapshot.getKey();
        String latitudeValue = String.valueOf(dataSnapshot.child("latitude").getValue());
        String longitudeValue = String.valueOf(dataSnapshot.child("longitude").getValue());

        // Cập nhật dữ liệu cho từng key
        dataMap.put(key, nameValue);

        // Hiển thị key và value trong CardView
        holder.keyTextView.setText(nameValue);
        holder.dataTextView.setText(dateValue);


        // Xác định nút "Hiển thị vị trí" cho cardview hiện tại
        Button locationButton = holder.itemView.findViewById(R.id.showLocationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem cả hai node "latitude" và "longitude" có tồn tại không
                if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    showLocationOnMap(key, latitudeValue, longitudeValue);
                } else {
                    Toast.makeText(context, "Không có dữ liệu vị trí", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xác định nút "Xem chi tiết" cho cardview hiện tại
        Button viewButton = holder.itemView.findViewById(R.id.viewButton);
        viewButton.setOnClickListener(v -> showDialog(key,passwordValue, nameValue,dateValue));

    }

    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView keyTextView;
        public TextView dataTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.keyTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
        }
    }

    private void showDialog(String key,String passwordValue, String nameValue, String dateValue) {
        // Tạo Dialog và hiển thị dữ liệu trong đó
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thông tin chi tiết");
        builder.setMessage("Email: " + key + "\nPassWord:"+ passwordValue+ "\nTên: " + nameValue +"\nNăm sinh: "+dateValue);
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showLocationOnMap( String key,String latitudeValue, String longitudeValue) {
        if (latitudeValue != null && !latitudeValue.isEmpty() && longitudeValue != null && !longitudeValue.isEmpty()) {
            // Chuyển sang Activity khác để hiển thị vị trí trên bản đồ
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("key",key);
            intent.putExtra("email",userEmail);
            intent.putExtra("latitude", Double.parseDouble(latitudeValue));
            intent.putExtra("longitude", Double.parseDouble(longitudeValue));
            context.startActivity(intent);
        } else {
            // Xử lý trường hợp dữ liệu không hợp lệ (ví dụ: chuỗi rỗng hoặc null)
            Toast.makeText(context, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}


