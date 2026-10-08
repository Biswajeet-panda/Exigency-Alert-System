package com.example.exigence;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import com.example.exigence.R;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class shake3 extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback{

    FusedLocationProviderClient fusedLocationProviderClient ;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapView mMapView;
    private GoogleMap mMap;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isAccelerometerEnabled = false;
    private float accelerationThreshold = 38;

    private TextView phoneNumberEditText;
    private Button sendSmsButton;
    private String phoneNumber;
    private String message;
    private TextView et_phone_number,textView_maps;
    private final static int REQUEST_CODE = 100;
    private String country, city, address, longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake3);

        //getCurrentLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Initialize the MapView
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync((OnMapReadyCallback) this);


        // Set rounded corners for the MapView
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadii(new float[]{50, 50, 50, 50, 0, 0, 0, 0});
        mMapView.setBackground(gd);

        et_phone_number = findViewById(R.id.et_phone_number);
        textView_maps = findViewById(R.id.textView_maps);


        // Retrieve phone number from intent
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("PHONE_NUMBER");
        message = intent.getStringExtra("MESSAGE");
        SharedPreferences SP = getSharedPreferences("myref", Context.MODE_PRIVATE);
//        message = SP.getString("MESSAGE", "");
        et_phone_number.setText(phoneNumber);


        phoneNumberEditText = findViewById(R.id.et_phone_number);
        sendSmsButton = findViewById(R.id.btn_send_sms);

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Location
                getLastLocation();

                String phoneNumber = phoneNumberEditText.getText().toString();
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(shake3.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                } else {
                    if (ContextCompat.checkSelfPermission(shake3.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendSms(phoneNumber);
                        sendSms2(phoneNumber);

                    } else {
                        ActivityCompat.requestPermissions(shake3.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    }
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerEnabled = true;
        } else {
            Toast.makeText(this, "Accelerometer is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a default location and zoom level
        LatLng defaultLocation = new LatLng(37.7749, -122.4194);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(defaultLocation)
                .zoom(12)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Enable location layer on map
            mMap.setMyLocationEnabled(true);

            // Get last known location and move camera there
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
                            }
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(com.example.exigence.shake3.this, Locale.getDefault());
                        List<Address> addresses= null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latitude = (""+ addresses.get(0).getLatitude());
                            longitude = ("" + addresses.get(0).getLongitude());
                            address = ("Address: " + addresses.get(0).getAddressLine(0));
                            city = ("City: " + addresses.get(0).getLocality());
                            country = ("Country: " + addresses.get(0).getCountryName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
        } else {
            askPermission();
        }
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(com.example.exigence.shake3.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            } else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void sendSms(String phoneNumber) {

        SharedPreferences SP = getSharedPreferences("mypref", MODE_PRIVATE);
        String message = SP.getString("MESSAGE", "");
        String name = SP.getString("NAME", "");
        String messagePrefix = "SOS! Please send help immediately!\nName-"+name+"\n"+city+"\nLocation- https://www.google.com/maps/search/"+latitude+","+longitude+"\n\n";
        String messageToSend = message != null ? messagePrefix + message : messagePrefix;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, messageToSend, null, null);
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void sendSms2(String phoneNumber) {

        SharedPreferences SP = getSharedPreferences("mypref", MODE_PRIVATE);
        String message = SP.getString("MESSAGE", "");
        String name = SP.getString("NAME", "");
        String messagePrefix = "Hey I am in Big Trouble, I need immediate help. \nName- "+name+"\n"+city+"\n"+address+"\n";
        String messageToSend = message != null ? messagePrefix + message : messagePrefix;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, messageToSend, null, null);
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration = Math.sqrt(x * x + y * y + z * z);
        if (acceleration > accelerationThreshold) {
            String phoneNumber = phoneNumberEditText.getText().toString();
            if (!phoneNumber.isEmpty() && ContextCompat.checkSelfPermission(shake3.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSms(phoneNumber);
                sendSms2(phoneNumber);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerEnabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccelerometerEnabled) {
            sensorManager.unregisterListener(this);
        }
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
