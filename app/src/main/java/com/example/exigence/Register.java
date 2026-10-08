package com.example.exigence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    private Button button_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText editTextEnterNumber = findViewById(R.id.editTextEnter_number);
        TextView textView = findViewById(R.id.registered_number);
        EditText editTextMessage = findViewById(R.id.editTextEnter_message);

        EditText editTextName = findViewById(R.id.editTextEnter_name);
        button_register = findViewById(R.id.button_register);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextEnterNumber.getText().toString();
                String message  = editTextMessage.getText().toString();
                String name  = editTextName.getText().toString();
                Intent intent = new Intent(Register.this, shake3.class);
                intent.putExtra("PHONE_NUMBER", phoneNumber);
                intent.putExtra("MESSAGE", message);
                intent.putExtra("NAME", name);

                if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                }

                SharedPreferences SP = getSharedPreferences("mypref", MODE_PRIVATE);
                SharedPreferences.Editor  edit = SP.edit();
                edit.putString("PHONE_NUMBER", phoneNumber);
                edit.putString("MESSAGE", message);
                edit.putString("NAME", name);
                edit.apply();

                editTextEnterNumber.setText(phoneNumber);
                editTextMessage.setText(message);
                editTextName.setText(name);
                textView.setText(phoneNumber);
                startActivity(intent);

            }
        });

        SharedPreferences SP = getSharedPreferences("mypref", MODE_PRIVATE);
        String phoneNumber = SP.getString("PHONE_NUMBER", "");
        String message = SP.getString("MESSAGE", "");
        String name = SP.getString("NAME", "");
        editTextEnterNumber.setText(phoneNumber);
        editTextMessage.setText(message);
        editTextName.setText(name);
        textView.setText(phoneNumber);

        String firstTime = SP.getString("FirstTimeInstall","");

        if(firstTime.equals("Yes")){
            if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
            Intent intent = new Intent(Register.this,shake3.class);
            intent.putExtra("PHONE_NUMBER", phoneNumber);
            startActivity(intent);
        }else {
            SharedPreferences.Editor editor = SP.edit();
            editor.putString("FirstTimeInstall", "Yes");
            editor.apply();

            if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Register.this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Register.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

