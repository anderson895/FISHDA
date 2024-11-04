package com.project.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.fish.R;

import java.util.ArrayList;
import java.util.List;

public class UserSearchFish extends AppCompatActivity {

    EditText searchField;
    TextView searchButton;
    ListView searchResultsListView;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search_fish);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Search Fish");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchField = findViewById(R.id.search);
        searchButton = findViewById(R.id.search_button);
        searchResultsListView = findViewById(R.id.search_results_list);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

        searchButton.setOnClickListener(view -> {
            String searchText = searchField.getText().toString().trim();

            if (!TextUtils.isEmpty(searchText)) {
                // Perform search query
                Query searchQuery = databaseReference.orderByChild("itemName").equalTo(searchText);
                searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Handle search results
                        if (dataSnapshot.exists()) {
                            // Clear previous results
                            searchResultsListView.setAdapter(null);

                            // Create a list to hold FishItems
                            List<FishItem> fishItemList = new ArrayList<>();

                            // Loop through search results
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // Handle each item found
                                String itemName = snapshot.child("itemName").getValue().toString();
                                String price = snapshot.child("itemPrice").getValue().toString();
                                String description = snapshot.child("itemDescription").getValue().toString();
                                String quantity = snapshot.child("itemQuantity").getValue().toString();

                                // Create a FishItem object
                                FishItem fishItem = new FishItem(itemName, price, description, quantity);
                                fishItemList.add(fishItem);
                            }

                            // Create the adapter to convert the array to views
                            FishAdapter adapter = new FishAdapter(UserSearchFish.this, fishItemList);

                            // Attach the adapter to a ListView
                            searchResultsListView.setAdapter(adapter);
                        } else {
                            searchResultsListView.setAdapter(null);

                            Toast.makeText(UserSearchFish.this, "No items found", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UserSearchFish.this, "Search canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UserSearchFish.this, "Please enter search text", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
