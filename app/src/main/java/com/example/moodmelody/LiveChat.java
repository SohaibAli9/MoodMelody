package com.example.moodmelody;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.FirebaseDatabaseKtxRegistrar;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

public class LiveChat extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String currentUser;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseUser user;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    String[] codingList = {"Muneeb", "Farhan", "Sameer", "Shahryar", "Salman", "Afnan", "Sohaib", "Kage", "Kumail", "Mohid", "Hassaan", "Umaid", "Fozan", "Rohit"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_live_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            mAuth=FirebaseAuth.getInstance();
            currentUser=mAuth.getUid();
            database = FirebaseDatabase.getInstance("https://moodmelody-cbf46-default-rtdb.asia-southeast1.firebasedatabase.app/");
            reference = database.getReference("Email");
            user= FirebaseAuth.getInstance().getCurrentUser();
            mAuth = FirebaseAuth.getInstance();
            Log.d("Tag", "Username: 1" + user.getEmail().toString());


            listView = findViewById(R.id.listView);
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_customtext, codingList);
            listView.setAdapter(arrayAdapter);
            return insets;
        });
        Log.d("Tag", "Username: 1");

        retrieveData();
    }
    private void retrieveData() {
        Log.d("TAG", "retrieveData: Retrieving data from Firebase");
        try {
            if (reference != null) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                Log.d("TAG", "onDataChange: Data received from Firebase");
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String key = dataSnapshot.getKey();
                                    String value = dataSnapshot.getValue(String.class);
                                    Log.d("TAG", "DataSnapshot key: " + key + ", value: " + value);
                                }
                            } else {
                                Log.d("TAG", "onDataChange: No data found at the reference");
                            }
                        } catch (Exception e) {
                            Log.e("TAG", "onDataChange: Exception occurred: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        try {
                            Log.e("TAG", "onCancelled: Database error: " + error.getMessage());
                        } catch (Exception e) {
                            Log.e("TAG", "onCancelled: Exception occurred: " + e.getMessage());
                        }
                    }
                });
            } else {
                Log.e("TAG", "retrieveData: Reference is null. Firebase not properly initialized or reference not set.");
            }
        } catch (Exception e) {
            Log.e("TAG", "retrieveData: Exception occurred while setting up the listener: " + e.getMessage());
            String test="123";
            reference.child(test).setValue("email");

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}





