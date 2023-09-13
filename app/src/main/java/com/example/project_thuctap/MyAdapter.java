package com.example.project_thuctap;

// MyAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Map<String, String> dataMap = new HashMap<>();
    private List<DataSnapshot> dataSnapshots;

    public MyAdapter(List<DataSnapshot> dataSnapshots) {
        this.dataSnapshots = dataSnapshots;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataSnapshot dataSnapshot = dataSnapshots.get(position);
        String key = dataSnapshot.getKey();

        // Lấy danh sách con của dataSnapshot và hiển thị chúng
        StringBuilder data = new StringBuilder();
        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            String childKey = childSnapshot.getKey();
            String childValue = String.valueOf(childSnapshot.getValue());
            data.append(childKey).append(": ").append(childValue).append("\n");
        }

        // Cập nhật dữ liệu cho từng key
        dataMap.put(key, data.toString());
        // Hiển thị key và value trong CardView
        holder.keyTextView.setText("Người: " + key);
        holder.dataTextView.setText(dataMap.get(key));
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
}


