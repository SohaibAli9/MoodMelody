package com.example.moodmelody;

import okhttp3.*;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeatherScreen extends AppCompatActivity {

    private final String appid = "f34bfbd84036b042371bd797d08c512c";
    DecimalFormat df = new DecimalFormat("#.#");
    FusedLocationProviderClient fusedLocationProviderClient;
    String Country, City, Address, Longitude = null, Latitude = null;
    private final static int REQUEST_CODE = 100;
    private TextView tvCityName;
    private TextView tvTemperature;
    private TextView tvFeelsLike;
    private TextView tvWind;
    private TextView tvHumidity, tvSuggestion;

    String output = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_screen);


        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvWind = findViewById(R.id.tvWind);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvSuggestion = findViewById(R.id.tvSuggestion);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        get_last_location();
        get_current_weather_data();

        Log.d("TAG", "onCreate: " + output);
    }

    void callAPI(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-4o");
            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Music");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);

            messages.put(systemMessage);
            messages.put(userMessage);

            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("TAG", "callAPI: Sending the request");
        // Create the request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/chat/completions",
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray choices = response.getJSONArray("choices");
                            String result = choices.getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");
                            Log.d("TAG", "onResponse: " + result);
                            tvSuggestion.setText(result);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("TAG", "onResponse Error1: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("TAG", "onResponse Error: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " );
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void get_current_weather_data() {
        if (Longitude == null && Latitude == null) {
            Latitude = "31.5204";
            Longitude = "74.3587";
        }
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + Latitude + "&lon=" + Longitude + "&exclude=hourly,daily&appid=" + appid;
        Log.d("TAG", "get_current_weather_data: " + Longitude + " " + Latitude);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "onResponse: " + response);
                Toast.makeText(WeatherScreen.this, response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    output += "Current weather of " + cityName + " (" + countryName + ")"
                            + "\n Temp: " + df.format(temp) + " °C"
                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
                            + "\n Humidity: " + humidity + "%"
                            + "\n Description: " + description
                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";
                    tvCityName.setText(cityName);
                    tvTemperature.setText(df.format(temp) + " °C");
                    tvFeelsLike.setText("Feels like: " + df.format(feelsLike) + " °C");
                    tvWind.setText("Wind: " + wind + "m/s (meters per second)");
                    tvHumidity.setText("Humidity: " + humidity + "%");

                    callAPI("Be vvvv concise in your answer, i want to show this on a popup so it should fit. " +
                            "This is the current weather and location data, recommend me some songs based" +
                            " on local genre and weather. the response should be three songs in the format song - artist and " +
                            " one line for reason why you chose these for locality and weather" + output);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WeatherScreen.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onErrorResponse: " + error.toString().trim());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void get_last_location() {
        Log.d("TAG", "get_last_location: On Location");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "get_last_location: Getting Permission");
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.d("TAG", "onSuccess: Getting location");
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(WeatherScreen.this, Locale.getDefault());
                        try {
                            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Latitude = String.valueOf(addresses.get(0).getLatitude());
                            Longitude = String.valueOf(addresses.get(0).getLongitude());
                            Address = String.valueOf(addresses.get(0).getAddressLine(0));
                            City = String.valueOf(addresses.get(0).getLocality());
                            Country = String.valueOf(addresses.get(0).getCountryName());

                            Log.d("TAG", "onSuccess: " + Latitude + " " + Longitude + "bhai");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d("TAG", "onSuccess: Location is null");
                        Latitude = "31.5204";
                        Longitude = "74.3587";
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(WeatherScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                get_last_location();
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}