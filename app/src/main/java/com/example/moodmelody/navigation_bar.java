package com.example.moodmelody;

import android.content.Intent;
import android.os.Bundle;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class navigation_bar extends AppCompatActivity {

    BottomNavigationView nav_bar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation_bar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; //placeholder
        });

        nav_bar = findViewById(R.id.bottomNavigationView);



//        nav_bar.setOnItemSelectedListener(item -> {
//
//            switch (item.getItemId()){
//
//                case R.id.Home:
//                    intent = new Intent(this, LoginScreen.class);
//                    startActivity(intent);
//                    break;
//                case R.id.Face:
//                    intent = new Intent(this, CameraTakePicture.class);
//                    startActivity(intent);
//                    break;
//                case R.id.Weather:
//                    intent = new Intent(this, WeatherScreen.class);
//                    startActivity(intent);
//                    break;
//                case R.id.Setting:
//                    intent = new Intent(this, LiveChat.class);
//                    startActivity(intent);
//                    break;
//            }
//
//            return true;
//        });
    }
}