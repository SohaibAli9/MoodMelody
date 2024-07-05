package com.example.moodmelody;

import com.example.moodmelody.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpScreen extends AppCompatActivity {
    String currentUser;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseUser user;


    TextInputLayout txt_username, txt_email, txt_password, txt_confirm_password;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        database = FirebaseDatabase.getInstance("https://moodmelody-cbf46-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("Email");
        user= FirebaseAuth.getInstance().getCurrentUser();

        txt_username = findViewById(R.id.txt_signup_username);
        txt_email = findViewById(R.id.txt_signup_email);
        txt_password = findViewById(R.id.txt_signup_pass);
        txt_confirm_password = findViewById(R.id.txt_signup_confirm_pass);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    public void register_button_clicked(View view) {
        String username, email, password, confirm_password;
        email = String.valueOf(txt_email.getEditText().getText());
        username = String.valueOf(txt_username.getEditText().getText());
        password = String.valueOf(txt_password.getEditText().getText());
        confirm_password = String.valueOf(txt_confirm_password.getEditText().getText());

        Context passing_context = this;
        if(validating_credentials(username, email, password, confirm_password, passing_context))
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(passing_context, "User Successfully Created", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();

                                Intent intent = new Intent(passing_context, SelectLogin.class);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(passing_context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        reference.child(email.replace(".", ",")).setValue(username);

    }

    private static boolean validating_credentials(String username, String email, String password, String confirm_password, Context context) {
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(context, "Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(confirm_password))
        {
            Toast.makeText(context, "Confirm Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!TextUtils.equals(password, confirm_password))
        {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(context, "Username is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(context, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void signin_button_clicked(View view) {

    }
}