package com.example.moodmelody;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chat_box extends AppCompatActivity {
    EditText txtmessage;
    TextView textView2;
    private FirebaseAuth mAuth;
    String currentUser;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtmessage = findViewById(R.id.editTextText);
        textView2 = findViewById(R.id.textView2);

        if (txtmessage == null || textView2 == null) {
            Log.e("Chat_box", "TextInputEditText or TextView is null!");
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getUid();
        database = FirebaseDatabase.getInstance("https://moodmelody-cbf46-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Email/");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        // Setup listener to display messages in textView2
        displayMessages();
    }

    public void Send_Message(View view) {
        String message = txtmessage.getText().toString();
        Log.d("Chat_box", "Send_Message: " + message);


        Intent intent = getIntent();
        String RecipientEmail = intent.getStringExtra("ToMessageEmail");

        String userEmail = user.getEmail();


        String[] parts = userEmail.split("@");
        String username = parts[0];

        String FinalMessage=username + ": "+message;

        String Combined = userEmail.replace(".", ",") + " " + RecipientEmail.replace(".", ",");
        String Combined2 =  RecipientEmail.replace(".", ",")+" "+userEmail.replace(".", ",");

        Log.d("Chat_box", "Send_Message: " + Combined);

        // Save message in Firebase
        reference.child(userEmail.replace(".", ",")).child(Combined).push().setValue(FinalMessage);
        reference.child(RecipientEmail.replace(".", ",")).child(Combined2).push().setValue(FinalMessage);

        // Clear message text field after sending
        txtmessage.setText("");
    }

    private void displayMessages() {
        // Listener to fetch and display messages
        reference.child(user.getEmail().replace(".", ","))
                .child(user.getEmail().replace(".", ",") + " " + getIntent().getStringExtra("ToMessageEmail").replace(".", ","))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StringBuilder messages = new StringBuilder();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String message = dataSnapshot.getValue(String.class);
                            messages.append(message).append("\n");
                        }
                        textView2.setText(messages.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Chat_box", "Failed to read value.", error.toException());
                    }
                });
    }
}
