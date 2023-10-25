// friends

package com.example.project_thuctap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Objects;

public class Friends extends AppCompatActivity {
    String email;
    String nameuser;
    String phoneuser;
    String emailuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Button search = findViewById(R.id.search);
        EditText inputsearch = findViewById(R.id.inputsearch);
        ListView friendsListView = findViewById(R.id.listViewFriends);
        ArrayList<FriendModel> friendsList = new ArrayList<>();
        // Tìm TextView theo tên



        DatabaseReference database = FirebaseDatabase.getInstance().getReference("admin/");

        email = getIntent().getStringExtra("email");
        emailuser = getIntent().getStringExtra("emailuser");
        phoneuser = getIntent().getStringExtra("phoneuser");
        nameuser = getIntent().getStringExtra("nameuser");

        DatabaseReference databasefriends = FirebaseDatabase.getInstance().getReference("admin/"+email+"/Friends");

        // hiển thị danh sách bạn bè
        databasefriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();

                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    String friendlist = snapshot2.getKey();

                    // Tạo một FriendModel từ dữ liệu bạn đã tìm thấy
                    String email = snapshot2.child("email").getValue(String.class);
                    String name = snapshot2.child("name").getValue(String.class);
                    String phone = snapshot2.child("phone").getValue(String.class);

                    int reply = 0; // Giá trị mặc định nếu trường "reply" không tồn tại
                    if (snapshot2.child("reply").exists()) {
                        reply = snapshot2.child("reply").getValue(Integer.class); // Lấy giá trị của reply
                    }


                    // Chuyển kiểu FriendModel thành NodeModel ở đây
                    FriendModel friend = new FriendModel(email, name, phone, reply);

                    if (reply == 1) {
                        friendsList.add(friend);
                    }
                }

                FriendAdapter adapter = new FriendAdapter(Friends.this, friendsList);
                adapter.setEmail(email); // Đặt giá trị email cho adapter
                ListView friendsListView = findViewById(R.id.listViewFriends);
                friendsListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi truy vấn dữ liệu từ Firebase
            }
        });



        // tim kiem ban be
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailtimthay = inputsearch.getText().toString().trim();

                String emailtoSearch = emailtimthay.replaceAll("[.@\\s]+", "").toLowerCase();

                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Lấy tên của node hiện tại
                            String adminName = snapshot.getKey();
                            if (emailtoSearch.equals(adminName)) {
                                found = true;
                                // Tìm thấy node trùng, có thể hiển thị thông tin ở đây hoặc thực hiện các hành động cần thiết
                                String emailValue = snapshot.child("/email").getValue(String.class);
                                String nameValue = snapshot.child("/name").getValue(String.class);
                                String phoneValue = Objects.requireNonNull(snapshot.child("/phone").getValue()).toString();
                                // Hiển thị thông tin trong Dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(Friends.this);
                                builder.setTitle("Thông tin chi tiết");
                                builder.setMessage("Email: " + emailValue + "\nTên: " + nameValue + "\nPhone: " + phoneValue);
                                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Kết bạn", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference().child("admin/"+emailtoSearch+"/Friends/Response/"+email);
                                        DatabaseReference addfriends = FirebaseDatabase.getInstance().getReference().child("admin/"+email+"/Friends/"+emailtoSearch);

                                        friendRequestRef.child("reply").setValue(0);
                                        friendRequestRef.child("name").setValue(nameuser);
                                        friendRequestRef.child("email").setValue(emailuser);
                                        friendRequestRef.child("phone").setValue(phoneuser);

                                        addfriends.child("reply").setValue(0);
                                        addfriends.child("name").setValue(nameValue);
                                        addfriends.child("email").setValue(emailValue);
                                        addfriends.child("phone").setValue(phoneValue);


                                        Toast.makeText(Friends.this,"Gửi lời mời thành công!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.show();
                                break; // Khi tìm thấy node trùng, thoát vòng lặp
                            }
                        }
                        if (!found) {
                            Toast.makeText(Friends.this,"Không tìm thấy email",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


        // hiển thị lời mời kết bạn
        Button listresponse = findViewById(R.id.listresponse);
        listresponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Friends.this);
                builder.setTitle("Danh sách lời mời kết bạn");

                // Tạo một layout cho AlertDialog để hiển thị danh sách lời mời kết bạn
                LinearLayout layout = new LinearLayout(Friends.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                database.child(email + "/Friends/Response/").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int margin = 20; // Khoảng cách giữa các CardView
                        int cardHeight = 300; // Đặt chiều cao của CardView

                        for (DataSnapshot snapshotUser : snapshot.getChildren()) {
                            // Đọc thông tin từ mỗi lời mời kết bạn
                            String responseEmail = snapshotUser.child("email").getValue(String.class);
                            String responseName = snapshotUser.child("name").getValue(String.class);
                            String responsePhone = snapshotUser.child("phone").getValue(String.class);

                            // Tạo một CardView cho mỗi lời mời kết bạn
                            CardView cardView = new CardView(Friends.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    cardHeight // Đặt chiều cao của CardView
                            );
                            layoutParams.setMargins(margin, margin, margin, margin); // Đặt khoảng cách
                            cardView.setLayoutParams(layoutParams);
                            cardView.setCardBackgroundColor(Color.GRAY);
                            cardView.setRadius(16);
                            cardView.setElevation(8);

                            // Tạo một LinearLayout bên trong CardView để chứa nút "Đồng ý" và "Xóa"
                            LinearLayout innerLayout = new LinearLayout(Friends.this);
                            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                            innerLayout.setLayoutParams(new CardView.LayoutParams(
                                    CardView.LayoutParams.MATCH_PARENT,
                                    CardView.LayoutParams.MATCH_PARENT // Đặt chiều cao cho innerLayout
                            ));

                            // Tạo một TextView để hiển thị thông tin lời mời kết bạn
                            TextView textView = new TextView(Friends.this);
                            textView.setLayoutParams(new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1
                            ));
                            textView.setPadding(16, 16, 16, 16);
                            textView.setText("Email: " + responseEmail + "\nTên: " + responseName + "\nPhone: " + responsePhone);
                            textView.setTextColor(Color.BLACK); // Đổi màu chữ thành màu đen

                            // Đặt các thuộc tính cho TextView
                            textView.setSingleLine(false); // Cho phép hiển thị nhiều dòng
                            textView.setMaxLines(3); // Số dòng tối đa
                            textView.setEllipsize(TextUtils.TruncateAt.END);

                            // Tạo nút "Đồng ý"
                            Button acceptButton = new Button(Friends.this);
                            acceptButton.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            acceptButton.setText("Đồng ý");
                            acceptButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String emaildongy = responseEmail.replaceAll("[.@\\s]+", "").toLowerCase();

                                    database.child(emaildongy+"/Friends/"+email+"/reply").setValue(1);
                                    database.child(email+"/Friends/"+emaildongy+"/reply").setValue(1);
                                    database.child(email+"/Friends/"+emaildongy+"/email").setValue(responseEmail);
                                    database.child(email+"/Friends/"+emaildongy+"/name").setValue(responseName);
                                    database.child(email+"/Friends/"+emaildongy+"/phone").setValue(responsePhone);
                                    database.child(email+"/Friends/Response/"+emaildongy).removeValue();
                                    Toast.makeText(Friends.this,"Kết bạn thành công!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Tạo nút "Xóa"
                            Button deleteButton = new Button(Friends.this);
                            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));
                            deleteButton.setText("Xóa");
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String emaildongy = responseEmail.replaceAll("[.@\\s]+", "").toLowerCase();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("admin/"+email+"/Friends"+"/Response/"+emaildongy);
                                    showLoadingDialog();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            databaseReference.removeValue();
                                            Toast.makeText(Friends.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }, 2000);
                                }
                            });

                            innerLayout.addView(textView);
                            innerLayout.addView(acceptButton);
                            innerLayout.addView(deleteButton);
                            cardView.addView(innerLayout);
                            layout.addView(cardView);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                ScrollView scrollView = new ScrollView(Friends.this);
                scrollView.addView(layout);
                builder.setView(scrollView);

                builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        // nhan tin


    }













    private AlertDialog loadingDialog;

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Friends.this);
        builder.setMessage("Đang xóa...");
        loadingDialog = builder.create();
        loadingDialog.show();
    }
    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

}

