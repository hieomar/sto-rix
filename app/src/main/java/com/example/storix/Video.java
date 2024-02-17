package com.example.storix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Script;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class Video extends AppCompatActivity {

     RecyclerView recyclerView;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        recyclerView = findViewById(R.id.videos_recycler_view);
        viewUploadedVideoContent();
    }

    protected void viewUploadedVideoContent() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseStorage.getInstance().getReference("uploads")
                .child(userId).child("videos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        List<UploadedFiles> videoList = new ArrayList<>();
                        VideoAdapter videoAdapter = new VideoAdapter(Video.this, videoList);

                        videoAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onItemClick(UploadedFiles video) {
                                String videoUrl = video.getFileUrl();
                                new CheckUrlTask() {
                                    @Override
                                    protected void onPostExecute(Boolean isAccessible) {
                                        if (isAccessible) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                                            intent.setDataAndType(Uri.parse(videoUrl), "video/*");
                                            startActivity(intent);
                                        } else {
                                            Log.d("Video URL", "URL is not accessible: " + videoUrl);
                                        }
                                    }
                                }.execute(videoUrl);
                            }
                        });

                        recyclerView.setAdapter(videoAdapter);
                        listResult.getItems().forEach(new Consumer<StorageReference>() {
                            @Override
                            public void accept(StorageReference storageReference) {
                                UploadedFiles video = new UploadedFiles();
                                video.setFileName(storageReference.getName());


                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                                @Override
                                                public void onSuccess(StorageMetadata storageMetadata) {
                                                    String fileSize = String.valueOf(storageMetadata.getSizeBytes());
                                                    fileSize = getFileSize(Long.parseLong(fileSize));
                                                    String fileUploadedDate = String.valueOf(storageMetadata.getCreationTimeMillis());
                                                    fileUploadedDate = convertMillisToDate(Long.parseLong(fileUploadedDate));

                                                    String videoUrl = task.getResult().toString();
                                                    video.setFileUrl(videoUrl);
                                                    video.setFileUploadedDate(fileUploadedDate);
                                                    video.setFileSize(fileSize);

                                                    Log.d("date", "date: " + video.getFileUploadedDate());
                                                    Log.d("size", "size: " + video.getFileSize());
                                                    videoList.add(video);
                                                    videoAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            Log.d("Video URL", "Failed to get download URL: " + task.getException());
                                        }
                                    }
                                });                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Video.this, "Failed to retrieve videos", Toast.LENGTH_SHORT).show();
                    }
                });

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