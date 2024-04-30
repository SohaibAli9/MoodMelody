package com.example.moodmelody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login);
    }

    public void LoginScreen(View view) {
        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }

    public void SignUpScreen(View view) {
        Intent intent = new Intent(this, SignUpScreen.class);
        startActivity(intent);
    }
}