package com.example.storix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Image extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        viewUploadedImageContent();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        recyclerView = findViewById(R.id.img_recycler_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_documents) {
                startActivity(new Intent(getApplicationContext(), Documents.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @SuppressLint({"StaticFieldLeak", "NotifyDataSetChanged"})
    private void viewUploadedImageContent() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseStorage.getInstance().getReference("uploads")
                .child(userId).child("images").listAll().addOnSuccessListener(listResult -> {
                    List<UploadedFiles> imgList = new ArrayList<>();
                    ImageAdapter imageAdapter = new ImageAdapter(Image.this, imgList);

                    imageAdapter.setOnItemClickListener(image -> {
                        String imgUrl = image.getFileUrl();
                        new Video.CheckUrlTask() {
                            @Override
                            protected void onPostExecute(Boolean isAccessible) {
                                if (isAccessible) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));
                                    intent.setDataAndType(Uri.parse(imgUrl), "image/*");
                                    startActivity(intent);
                                } else {
                                    Log.d("Image URL", "URL is not accessible: " + imgUrl);
                                }
                            }
                        }.execute(imgUrl);
                    });

                    recyclerView.setAdapter(imageAdapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

                    listResult.getItems().forEach(storageReference -> {
                        UploadedFiles image = new UploadedFiles();
                        image.setFileName(storageReference.getName());

                        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                                    String fileSize = String.valueOf(storageMetadata.getSizeBytes());
                                    String fileUploadedDate = String.valueOf(storageMetadata.getCreationTimeMillis());
                                    fileUploadedDate = convertMillisToDate(Long.parseLong(fileUploadedDate));

                                    String videoUrl = task.getResult().toString();
                                    image.setFileUrl(videoUrl);
                                    image.setFileUploadedDate(fileUploadedDate);
                                    image.setFileSize(fileSize);

                                    imgList.add(image);
                                    // Sort the images by name
                                    imgList.sort((o1, o2) -> o1.getFileName().compareTo(o2.getFileName()));
                                    imageAdapter.notifyDataSetChanged();
                                });
                            } else {
                                Log.d("Document URL", "Failed to get download URL: " + task.getException());
                            }
                        });                            });
                }).addOnFailureListener(e -> Toast.makeText(Image.this, "Failed to retrieve documents", Toast.LENGTH_SHORT).show());

    }

    private String convertMillisToDate(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static class CheckUrlTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                return responseCode == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // result is the return value from doInBackground
            // you can update your UI here based on the result
        }
    }

}