package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class NewServiceActivity extends AppCompatActivity {

    private EditText etServiceName, etServicePrice;
    private Button btnSaveService;
    private ImageView serviceImageView;

    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_salone_service);

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("services");

        etServiceName = findViewById(R.id.et_service_name);
        etServicePrice = findViewById(R.id.et_service_price);
        btnSaveService = findViewById(R.id.btn_save_service);
        serviceImageView = findViewById(R.id.imageView_service);

        btnSaveService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addService();
            }
        });

        serviceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                serviceImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addService() {
        String serviceName = etServiceName.getText().toString().trim();
        String servicePrice = etServicePrice.getText().toString().trim();

        if (serviceName.isEmpty() || servicePrice.isEmpty()) {
            Toast.makeText(this, "Please enter service name and price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a new key for the service
        String serviceId = databaseReference.push().getKey();

        // Create a Service object with the entered details
        Service service = new Service(serviceId, serviceName, Double.parseDouble(servicePrice));

        // Save the service object to Firebase Realtime Database
        databaseReference.child(serviceId).setValue(service);

        // Upload the image to Firebase Storage
        if (selectedImageUri != null) {
            uploadImage(selectedImageUri, serviceId);
        }

        Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void uploadImage(Uri imageUri, final String serviceId) {
        // Get a reference to the Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("serviceImages");

        // Create a reference to the image file with the service ID
        final StorageReference imageRef = storageRef.child(serviceId + ".jpg");

        // Upload the image file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                Toast.makeText(NewServiceActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to upload image
                Toast.makeText(NewServiceActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
