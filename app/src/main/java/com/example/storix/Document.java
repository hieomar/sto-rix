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
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Document extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        recyclerView = findViewById(R.id.document_recycler_view);
        viewUploadedDocumentContent();

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
    private void viewUploadedDocumentContent() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseStorage.getInstance().getReference("uploads")
                .child(userId).child("documents").listAll().addOnSuccessListener(listResult -> {
                    List<UploadedFiles> documentList = new ArrayList<>();
                    DocumentAdapter documentAdapter = new DocumentAdapter(Document.this, documentList);

                    documentAdapter.setOnItemClickListener(document -> {
                        String documentUrl = document.getFileUrl();
                        new CheckUrlTask() {
                            @Override
                            protected void onPostExecute(Boolean isAccessible) {
                                if (isAccessible) {
                                    String mimeType = URLConnection.guessContentTypeFromName(documentUrl);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse(documentUrl), mimeType);
                                    startActivity(intent);
                                } else {
                                    Log.d("Document URL", "URL is not accessible: " + documentUrl);
                                }
                            }
                        }.execute(documentUrl);
                    });

                    recyclerView.setAdapter(documentAdapter);
                    listResult.getItems().forEach(storageReference -> {
                        UploadedFiles document = new UploadedFiles();
                        document.setFileName(storageReference.getName());


                        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                                    String fileSize = String.valueOf(storageMetadata.getSizeBytes());
                                    fileSize = getFileSize(Long.parseLong(fileSize));
                                    String fileUploadedDate = String.valueOf(storageMetadata.getCreationTimeMillis());
                                    fileUploadedDate = convertMillisToDate(Long.parseLong(fileUploadedDate));

                                    String videoUrl = task.getResult().toString();
                                    document.setFileUrl(videoUrl);
                                    document.setFileUploadedDate(fileUploadedDate);
                                    document.setFileSize(fileSize);

                                    documentList.add(document);
                                    // Sort the document list names in ascending order
                                    documentList.sort(Comparator.comparing(UploadedFiles::getFileName));
                                    documentAdapter.notifyDataSetChanged();
                                });
                            } else {
                                Log.d("Document URL", "Failed to get download URL: " + task.getException());
                            }
                        });                            });
                }).addOnFailureListener(e -> Toast.makeText(Document.this, "Failed to retrieve documents", Toast.LENGTH_SHORT).show());
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