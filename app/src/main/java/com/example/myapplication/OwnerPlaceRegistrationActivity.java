package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OwnerPlaceRegistrationActivity extends AppCompatActivity {

    private EditText placeNameEditText;
    private EditText placeAddressEditText;
    private EditText placeDescriptionEditText;
    private Button saveButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_place_registration);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the ownerId from the intent
        String ownerId = getIntent().getStringExtra("ownerId");

        // Initialize views
        placeNameEditText = findViewById(R.id.placeNameEditText);
        placeAddressEditText = findViewById(R.id.placeAddressEditText);
        placeDescriptionEditText = findViewById(R.id.placeDescriptionEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savePlaceDetails(ownerId);
                Intent intent = new Intent(OwnerPlaceRegistrationActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void savePlaceDetails(String ownerId) {
        // Retrieve the place details from the input fields
        String placeName = placeNameEditText.getText().toString();
        String placeAddress = placeAddressEditText.getText().toString();
        String placeDescription = placeDescriptionEditText.getText().toString();

        // Create a new document in the "Places" collection
        DocumentReference placeRef = db.collection("Places").document();

        // Create a HashMap to store the place details
        Map<String, Object> placeInfo = new HashMap<>();
        placeInfo.put("name", placeName);
        placeInfo.put("address", placeAddress);
        placeInfo.put("description", placeDescription);
        placeInfo.put("ownerId", ownerId); // Associate the owner with the place

        // Set the place information in the Firestore document
        placeRef.set(placeInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully written
                        Toast.makeText(OwnerPlaceRegistrationActivity.this, "Place details saved successfully.", Toast.LENGTH_SHORT).show();
                        // Optionally, you can navigate to another activity or perform further actions
                        finish(); // Finish the activity after saving the place details
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error writing document
                        Toast.makeText(OwnerPlaceRegistrationActivity.this, "Failed to save place details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
