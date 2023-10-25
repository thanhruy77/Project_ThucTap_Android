//NodeModel
package com.example.project_thuctap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
public class FriendAdapter extends ArrayAdapter<FriendModel> {
    private Context context;
    private ArrayList<FriendModel> friendsList;
    private String email;

    public FriendAdapter(Context context, ArrayList<FriendModel> friendsList) {
        super(context, 0, friendsList);
        this.context = context;
        this.friendsList = friendsList;
    }
    // Phương thức setter để thiết lập email từ bên ngoài
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendModel friend = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, parent, false);
        }


        // Bên trong phương thức getView
        ImageView messengerImageView = convertView.findViewById(R.id.messenger);

        // Lấy tham chiếu đến các TextView trong layout friend_item
        TextView emailTextView = convertView.findViewById(R.id.emailTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView phoneTextView = convertView.findViewById(R.id.phoneTextView);

        // Bắt sự kiện khi người dùng nhấn vào ImageView "messenger"
        messengerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtimthay = friend.getEmail();
                String emailnguoinhan = emailtimthay.replaceAll("[.@\\s]+", "").toLowerCase();

                // Tạo một Intent để mở lớp hoạt động mới
                Intent intent = new Intent(context, Chat.class);
                 intent.putExtra("emailnguoinhan", emailnguoinhan);
                 intent.putExtra("email",email);
                // Bắt đầu lớp hoạt động mới
                context.startActivity(intent);
            }
        });

        // Hiển thị dữ liệu bạn bè lên các TextView
        emailTextView.setText(friend.getEmail());
        nameTextView.setText(friend.getName());
        phoneTextView.setText(friend.getPhone());
        return convertView;
    }



}
