package com.example.exigence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView WelcomeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WelcomeText = findViewById(R.id.WelcomeText);
        WelcomeText.setText("Exigence!");
        new CountDownTimer(1000,500){
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                startActivity(new Intent(MainActivity.this, Register.class));
                MainActivity.this.finish();
            }
        }.start();
    }
}