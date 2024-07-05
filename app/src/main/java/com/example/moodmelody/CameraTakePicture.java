package com.example.moodmelody;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraTakePicture extends AppCompatActivity {

    ImageView imageView;
    Button takePic;
    private LinearLayout moodSection, songSuggestionsSection;
    private TextView moodText, suggestionsHeader, resultText;
    private SongAdapter songAdapter;
    private List<Song> songList;

    FirebaseVisionImage image;
    FirebaseVisionFaceDetector detector;
    private RecyclerView songSuggestionsRecyclerView;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_take_picture);

        Log.d("TAG", "onCreate: oneonone");
        imageView = findViewById(R.id.mood_icon);
        Log.d("TAG", "onCreate: oneonone2");
        takePic = findViewById(R.id.btn_takepic);
        Log.d("TAG", "onCreate: oneonone3");
        moodSection = findViewById(R.id.mood_section);
        Log.d("TAG", "onCreate: oneonone4");
        songSuggestionsSection = findViewById(R.id.song_suggestions_section);
        songSuggestionsRecyclerView = findViewById(R.id.music_recycler_view);
        Log.d("TAG", "onCreate: oneonone5");
        moodText = (TextView) findViewById(R.id.mood_text);
        resultText = findViewById(R.id.result_text);
        Log.d("TAG", "onCreate: oneonone6");
        suggestionsHeader = findViewById(R.id.suggestions_header);
        Log.d("TAG", "onCreate: oneonone7");
        Log.d("TAG", "onCreate: oneonone8");

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            }
        });
        Log.d("TAG", "onCreate: oneonone9");
        songList = new ArrayList<>();
        Log.d("TAG", "onCreate: oneonone10");
        songAdapter = new SongAdapter(songList);
        Log.d("TAG", "onCreate: oneonone11");
        songSuggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("TAG", "onCreate: oneonone12");
        songSuggestionsRecyclerView.setAdapter(songAdapter);
        Log.d("TAG", "onCreate: oneonone13");
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmap);
                    detectFace(bitmap);
                }
            }
    );

    private void detectFace(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // It’s time to prepare our Face Detection model.
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            float smileProb, leftEyeProb, rightEyeProb;
                @Override
                public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                    String resultText = "";
                    int i = 1;
                    for (FirebaseVisionFace face : firebaseVisionFaces) {
                        smileProb = face.getSmilingProbability();
                        leftEyeProb = face.getLeftEyeOpenProbability();
                        rightEyeProb = face.getRightEyeOpenProbability();

                        resultText = resultText.concat("\nFACE NUMBER. " + i + ": ")
                                .concat("\nSmile: " + smileProb * 100 + "%")
                                .concat("\nLeft eye open: " + leftEyeProb * 100 + "%")
                                .concat("\nRight eye open: " + rightEyeProb * 100 + "%");
                        i++;
                    }

                    if (firebaseVisionFaces.size() == 0) {
                        Toast.makeText(CameraTakePicture.this, "NO FACE DETECT", Toast.LENGTH_SHORT).show();
                    } else {
                        String mood = predictMood(smileProb, leftEyeProb, rightEyeProb);

                        moodSection.setVisibility(View.VISIBLE);
                        songSuggestionsSection.setVisibility(View.VISIBLE);

                        switch (mood) {
                            case "Happy":
                                imageView.setImageResource(R.drawable.happy_icon);
                                moodText.setText("You look happy!");
                                break;
                            case "Sad":
                                imageView.setImageResource(R.drawable.sad_icon);
                                moodText.setText("You look sad.");
                                break;
                            case "Neutral":
                                imageView.setImageResource(R.drawable.neutral_icon);
                                moodText.setText("You look neutral.");
                                break;
                            case "Angry":
                                imageView.setImageResource(R.drawable.angry_icon);
                                moodText.setText("You look angry.");
                                break;
                            case "Surprised":
                                imageView.setImageResource(R.drawable.surprised_icon);
                                moodText.setText("You look surprised.");
                                break;
                            // Add more cases for different moods
                        }

                        String prompt = "suggest 3 songs based on the " + mood + "mood. " +
                                "the answer should be 'song - artist - release year' and the three songs should be " +
                                "comma separated. there should be nothing else in the response" +
                                "other than the three comma separated songs";
                        callOpenAI(prompt, mood);
                        Log.d("TAG", "onSuccess: AFTER the request");
                    }
                }

                private String predictMood(float smileProb, float leftEyeProb, float rightEyeProb) {
                    if (smileProb > 0.7 && leftEyeProb > 0.5 && rightEyeProb > 0.5) {
                        return "Happy";
                    } else if (smileProb < 0.3) {
                        return "Sad";
                    } else if (smileProb >= 0.3 && smileProb <= 0.7) {
                        if (leftEyeProb >= 0.3 && rightEyeProb >= 0.3) {
                            return "Neutral";
                        } else if (leftEyeProb > 0.7 && rightEyeProb > 0.7) {
                            return "Surprised";
                        } else if (leftEyeProb < 0.3 && rightEyeProb < 0.3) {
                            return "Angry";
                        }
                    }
                    return "Neutral";
                }

                    private List<Song> setListView(String mood, String allSongs) {
                        // This method should call the GPT API and return a list of songs based on the detected mood
                        // For the sake of this example, we'll just return some dummy data
                        Log.d("TAG", "setListView: one");
                        String[] allSongsList = allSongs.split(", ");
                        Log.d("TAG", "setListView: two");
                        String[][] var = new String[allSongsList.length][3];
                        Log.d("TAG", "setListView: three");
                        for (int i = 0; i < allSongsList.length; i++) {
                            String[] songDetails = allSongsList[i].split("-");
                            var[i][0] = songDetails[0]; // Song name
                            var[i][1] = songDetails[1]; // Artist
                            var[i][2] = songDetails[2]; // Year
                        }
                        Log.d("TAG", "setListView: four" + "var[1][0] = " + var[1][0]);
                        List<Song> songs = new ArrayList<>();
                        songs.add(new Song(var[0][0], var[0][1], R.drawable.album_art_placeholder));
                        songs.add(new Song(var[1][0], var[1][1], R.drawable.album_art_placeholder));
                        songs.add(new Song(var[2][0], var[2][1], R.drawable.album_art_placeholder));
                        Log.d("TAG", "setListView: five");
                        return songs;
                    }

                    void callOpenAI(String question, String mood) {
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
                                        result = choices.getJSONObject(0).getJSONObject("message").getString("content");
                                        songList.clear();
                                        List<Song> songs = setListView(mood, result);
//                                        songList.addAll(songs);
                                        Log.d("TAG", "onResponse: new adapter");
//                                        songAdapter = new SongAdapter(songList);
                                        Log.d("TAG", "onResponse: setting adapter");
//                                        songSuggestionsRecyclerView.setAdapter(songAdapter);
                                        resultText.setText(result);
                                        Log.d("TAG", "onResponse: adapter set");
//                                        songAdapter.update_list(songList);
//                                        songAdapter.notifyDataSetChanged();
                                        Log.d("TAG", "onResponse: boutto run ui");
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                songAdapter.notifyDataSetChanged();
//                                            }
//                                        });
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
                            })
                            {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
//                                headers.put("Authorization", "Bearer " + API_KEY);
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };

                        // Add the request to the RequestQueue
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(jsonObjectRequest);
                    }

            }) // adding an onfailure listener as well if
            // something goes wrong.
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast
                            .makeText(
                                    CameraTakePicture.this,
                                    "Oops, Something went wrong",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });
    }
}