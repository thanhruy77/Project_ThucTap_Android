package com.example.project_thuctap;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.project_thuctap.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean isLocationDataReceived = false; // Biến kiểm tra dữ liệu đã sẵn sàng
    private LatLng trackedLocation; // Biến để lưu trữ vị trí đang theo dõi
    private String name ;
    private Dialog dialog;

    private Button toggleButton;
    double lautitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutsetting);

        DatabaseReference getlocation = FirebaseDatabase.getInstance().getReference();

        getlocation.child("Location/Lautitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lautitude = Double.parseDouble(snapshot.getValue().toString());
                updateMapLocation(); // Cập nhật vị trí trên bản đồ khi có dữ liệu mới.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getlocation.child("Location/Longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                longitude = Double.parseDouble(snapshot.getValue().toString());
                updateMapLocation(); // Cập nhật vị trí trên bản đồ khi có dữ liệu mới
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getlocation.child("Location/Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                if (value != null) {
                    name = value.toString();
                    updateMapLocation(); // Cập nhật vị trí trên bản đồ khi có dữ liệu mới
                } else {
                    // Xử lý khi giá trị là null
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // chuyển đổi chế độ map
        toggleButton = findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_normal:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.menu_satellite:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.menu_focus_location:
                        focusOnCurrentLocation(); // gọi hàm focusOnCurrentLocation
                        break;
                    case R.id.setting:
                        doithongtin(Gravity.CENTER);  // goi hàm doithongtin
                }
                return true;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Khởi tạo vị trí ban đầu
        LatLng initialLocation = new LatLng(lautitude, longitude);

        // Di chuyển camera tới vị trí mới và giữ nguyên chế độ zoom 15
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLocation, 15);
        mMap.moveCamera(cameraUpdate);

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void updateMapLocation() {
        if (mMap != null) {
            LatLng location = new LatLng(lautitude, longitude);
            trackedLocation = location; // Cập nhật vị trí đang theo dõi
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(location).title(name));

            // Di chuyển camera tới vị trí mới và giữ nguyên chế độ zoom
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, mMap.getCameraPosition().zoom);
            mMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_focus_location:
                focusOnCurrentLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void focusOnCurrentLocation() {
        if (mMap != null && trackedLocation != null) {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(trackedLocation, cameraPosition.zoom);
            mMap.moveCamera(cameraUpdate);
        }
    }


    private void doithongtin(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutsetting);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        EditText editText = dialog.findViewById(R.id.name);
        Button btnno = dialog.findViewById(R.id.no);
        Button btnyes = dialog.findViewById(R.id.yes);
        DatabaseReference finalQmkdatabase = FirebaseDatabase.getInstance().getReference().child("Location/Name");
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = editText.getText().toString();
                if (data.isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Bạn chưa nhập gì...", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    finalQmkdatabase.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MapsActivity.this, "Thay đổi thành công!!!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsActivity.this, "Lỗi không xác định!!!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
