package com.example.storix;


import androidx.appcompat.app.AppCompatActivity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Audio extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        recyclerView = findViewById(R.id.audio_recycler_view);

        viewUploadedAudioContent();

        // TODO: test this code
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
    private void viewUploadedAudioContent() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseStorage.getInstance().getReference("uploads")
                .child(userId).child("audios").listAll().addOnSuccessListener(listResult -> {
                    List<UploadedFiles> audioList = new ArrayList<>();
                    AudioAdapter audioAdapter = new AudioAdapter(Audio.this, audioList);

                    audioAdapter.setOnItemClickListener(audio -> {
                        String audioUrl = audio.getFileUrl();
                        new CheckUrlTask() {
                            @Override
                            protected void onPostExecute(Boolean isAccessible) {
                                if (isAccessible) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(audioUrl));
                                    intent.setDataAndType(Uri.parse(audioUrl), "audio/*");
                                    startActivity(intent);
                                } else {
                                    Log.d("Audio URL", "URL is not accessible: " + audioUrl);
                                }
                            }
                        }.execute(audioUrl);
                    });

                    recyclerView.setAdapter(audioAdapter);
                    listResult.getItems().forEach(storageReference -> {
                        UploadedFiles audio = new UploadedFiles();
                        audio.setFileName(storageReference.getName());

                        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                                    String fileSize = String.valueOf(storageMetadata.getSizeBytes());
                                    fileSize = getFileSize(Long.parseLong(fileSize));
                                    String fileUploadedDate = String.valueOf(storageMetadata.getCreationTimeMillis());
                                    fileUploadedDate = convertMillisToDate(Long.parseLong(fileUploadedDate));

                                    String videoUrl = task.getResult().toString();
                                    audio.setFileUrl(videoUrl);
                                    audio.setFileUploadedDate(fileUploadedDate);
                                    audio.setFileSize(fileSize);

                                    audioList.add(audio);
                                    audioAdapter.notifyDataSetChanged();
                                });
                            } else {
                                Log.d("Audio URL", "Failed to get download URL: " + task.getException());
                            }
                        });                            });
                }).addOnFailureListener(e -> Toast.makeText(Audio.this, "Failed to retrieve audio", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("DefaultLocale")
    private String getFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
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