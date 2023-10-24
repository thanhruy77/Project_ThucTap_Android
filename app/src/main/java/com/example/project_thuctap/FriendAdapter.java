//NodeModel
package com.example.project_thuctap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
public class FriendAdapter extends ArrayAdapter<FriendModel> {
    private Context context;
    private ArrayList<FriendModel> friendsList;

    public FriendAdapter(Context context, ArrayList<FriendModel> friendsList) {
        super(context, 0, friendsList);
        this.context = context;
        this.friendsList = friendsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendModel friend = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, parent, false);
        }

        // Lấy tham chiếu đến các TextView trong layout friend_item
        TextView emailTextView = convertView.findViewById(R.id.emailTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView phoneTextView = convertView.findViewById(R.id.phoneTextView);

        // Hiển thị dữ liệu bạn bè lên các TextView
        emailTextView.setText(friend.getEmail());
        nameTextView.setText(friend.getName());
        phoneTextView.setText(friend.getPhone());

        return convertView;
    }
}
