package com.example.moodmelody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ScreenTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_two);
    }

    public void Next_Screen(View view) {
        Intent intent = new Intent(this, ScreenThree.class);
        startActivity(intent);
    }

    public void SkipScreen(View view) {
        Intent intent = new Intent(this, SelectLogin.class);
        startActivity(intent);
    }
}