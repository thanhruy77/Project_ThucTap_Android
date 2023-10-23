package com.example.project_thuctap;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

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

import java.text.DecimalFormat;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean isLocationDataReceived = false; // Biến kiểm tra dữ liệu đã sẵn sàng
    private LatLng trackedLocation; // Biến để lưu trữ vị trí đang theo dõi
    private String name;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    private Button toggleButton;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String key = getIntent().getStringExtra("key");
        String email = getIntent().getStringExtra("email");
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DatabaseReference getlocation = FirebaseDatabase.getInstance().getReference();
        DatabaseReference getSpeed = FirebaseDatabase.getInstance().getReference().child("Speed");
        getlocation.child("admin/" + email + "/users/" + key + "/latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latitude = Double.parseDouble(snapshot.getValue().toString());
                updateMapLocation(); // Cập nhật vị trí trên bản đồ khi có dữ liệu mới.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getlocation.child("admin/" + email + "/users/" + key + "/longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                longitude = Double.parseDouble(snapshot.getValue().toString());
                updateMapLocation(); // Cập nhật vị trí trên bản đồ khi có dữ liệu mới
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Button viewspeed = findViewById(R.id.speed);
        getSpeed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double value = (double) snapshot.getValue();
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String fomated = decimalFormat.format(value);
                viewspeed.setText(fomated+" M/s");
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
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case R.id.menu_focus_location:
                        focusOnCurrentLocation(); // gọi hàm focusOnCurrentLocation
                        break;
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
        LatLng initialLocation = new LatLng(latitude, longitude);

        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Di chuyển camera tới vị trí mới và giữ nguyên chế độ zoom 13
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLocation, 13);
        mMap.moveCamera(cameraUpdate);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        updateMapLocation();
    }


    private void updateMapLocation() {
        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền truy cập vị trí đã được cấp, hiển thị vị trí người dùng trên bản đồ
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // Người dùng từ chối quyền truy cập vị trí, bạn có thể thông báo cho họ hoặc thực hiện xử lý khác
                Toast.makeText(this, "Ứng dụng cần quyền truy cập vị trí để hoạt động.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
