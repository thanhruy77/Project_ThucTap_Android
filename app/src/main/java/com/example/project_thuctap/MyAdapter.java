package com.example.project_thuctap;

// MyAdapter.java

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Map<String, String> dataMap = new HashMap<>();
    private List<DataSnapshot> dataSnapshots;
    private Context context;
    private String emailadmin;
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

    public void setEmailAdmin(String email) {
        this.emailadmin = email;
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
        String phoneValue = String.valueOf(dataSnapshot.child("phone").getValue());
        String key = dataSnapshot.getKey();
        String latitudeValue = String.valueOf(dataSnapshot.child("latitude").getValue());
        String longitudeValue = String.valueOf(dataSnapshot.child("longitude").getValue());

        // Cập nhật dữ liệu cho từng key
        dataMap.put(key, nameValue);

        // Hiển thị key và value trong CardView
        holder.keyTextView.setText(nameValue);
        holder.dataTextView.setText(phoneValue);


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
        viewButton.setOnClickListener(v -> showDialog(key,phoneValue, nameValue));

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

    private void showDialog(String key,String phoneVAlue, String nameValue) {
        // Tạo Dialog và hiển thị dữ liệu trong đó
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thông tin chi tiết");
        builder.setMessage("Email: " + key + "\nPhone:"+ phoneVAlue+ "\nTên: " + nameValue);
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Xóa người này", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("admin/"+emailadmin+"/users/"+key);
                databaseReference.child("deleteEEPROM").setValue(1);

                // Gọi hàm showLoadingDialog để hiển thị hộp thoại
                showLoadingDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        databaseReference.removeValue();
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }, 3000);
            }
        });
        builder.show();
    }

    private AlertDialog loadingDialog;

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Đang xóa...");
        loadingDialog = builder.create();
        loadingDialog.show();
    }
    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
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
            // Gọi overridePendingTransition sau khi startActivity
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            // Xử lý trường hợp dữ liệu không hợp lệ (ví dụ: chuỗi rỗng hoặc null)
            Toast.makeText(context, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}


