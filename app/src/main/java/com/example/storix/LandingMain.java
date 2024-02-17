package com.example.storix;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LandingMain extends AppCompatActivity {
    private StorageReference storageReference;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromRightToLeft;
    private Animation fromLeftToRight;

    FloatingActionButton fab;
    FloatingActionButton videoFab;
    FloatingActionButton audioFab;
    FloatingActionButton imageFab;
    FloatingActionButton documentFab;

    private boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_main);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        rotateOpen = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromRightToLeft = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.from_right_to_left_anim);
        fromLeftToRight = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.from_left_to_right_anim);

        MaterialCardView VideoCard = findViewById(R.id.video_card);
        MaterialCardView AudioCard = findViewById(R.id.audio_card);
        MaterialCardView ImageCard = findViewById(R.id.img_card);
        MaterialCardView DocumentCard = findViewById(R.id.document_card);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        // Get material floating action button
        fab = findViewById(R.id.add_fab);
        videoFab = findViewById(R.id.video_fab);
        audioFab = findViewById(R.id.audio_fab);
        imageFab = findViewById(R.id.img_fab);
        documentFab = findViewById(R.id.document_fab);

        fab.setOnClickListener(view -> onAddButtonClicked());

        videoFab.setOnClickListener(view -> openVideoSelector());

        audioFab.setOnClickListener(view -> openAudioSelector());

        imageFab.setOnClickListener(view -> openImageSelector());

        documentFab.setOnClickListener(view -> openDocumentSelector());

        VideoCard.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Video.class));
            overridePendingTransition(0, 0);
        });

        AudioCard.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Audio.class));
            overridePendingTransition(0, 0);
        });

        ImageCard.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Image.class));
            overridePendingTransition(0, 0);
        });

        DocumentCard.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Document.class));
            overridePendingTransition(0, 0);
        });

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

    private void openDocumentSelector() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //allow user to select multiple files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        picker.launch(intent);
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //allow user to select multiple files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        picker.launch(intent);
    }

    private void openAudioSelector() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //allow user to select multiple files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        picker.launch(intent);
    }

    private void openVideoSelector() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //allow user to select multiple files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        picker.launch(intent);
    }

    private final ActivityResultLauncher<Intent> picker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            uris.add(result.getData().getClipData().getItemAt(i).getUri());
                        }
                        showSelectedItems(uris, getFileExtension(uris.get(0)));
                    } else if (result.getData().getData() != null) {
                        ArrayList<Uri> uris = new ArrayList<>();
                        uris.add(result.getData().getData());
                        showSelectedItems(uris, getFileExtension(uris.get(0)));
                    }
                }
            });

    private void showSelectedItems(ArrayList<Uri> uris, String fileType) {
        StringBuilder message = new StringBuilder();
        for (Uri uri : uris) {
            message.append(getFileName(uri)).append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message.toString())
                .setNegativeButton(android.R.string.cancel, null);

        switch (fileType) {
            case "pdf":
            case "doc":
            case "docx":
            case "ppt":
            case "pptx":
            case "xls":
            case "xlsx":
            case "txt":
            case "rtf":
            case "csv":
            case "odt":
                builder.setTitle("Selected Document Files")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> uploadFiles(uris, "documents"));
                break;
            case "mp3":
            case "wav":
            case "flac":
            case "aac":
            case "ogg":
            case "m4a":
                builder.setTitle("Selected Audio Files")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> uploadFiles(uris, "audios"));
                break;
            case "mp4":
            case "mov":
            case "avi":
            case "mkv":
            case "wmv":
            case "mpeg":
                builder.setTitle("Selected Video Files")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> uploadFiles(uris, "videos"));
                break;
            case "jpg":
            case "png":
            case "gif":
            case "bmp":
            case "svg":
            case "webp":
                builder.setTitle("Selected Image Files")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> uploadFiles(uris, "images"));
                break;
            default:
                Toast.makeText(this, "File Formats not Accepted", Toast.LENGTH_SHORT).show();
                break;
        }

        builder.show();
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFiles(ArrayList<Uri> uris, String directory) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        int totalFiles = uris.size();
        AtomicInteger uploadedFiles = new AtomicInteger();

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Upload", "File Upload", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Upload")
                .setSmallIcon(R.drawable.storix)  // replace with your own icon
                .setContentTitle("Uploading Files")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(totalFiles, 0, false);

        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 1;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission error", "Permission not granted");
            return;
        } else {
            notificationManager.notify(notificationId, builder.build());
        }
        notificationManager.notify(notificationId, builder.build());

        for (Uri uri : uris) {
            StorageReference fileReference = storageReference.child(userId).child(directory).child(getFileName(uri));
            fileReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        uploadedFiles.getAndIncrement();
                        // Update the progress in the notification
                        builder.setProgress(totalFiles, uploadedFiles.get(), false);
                        notificationManager.notify(notificationId, builder.build());

                        if (uploadedFiles.get() == totalFiles) {
                            // When upload is complete, update the notification text and remove the progress bar
                            builder.setContentText("Upload complete")
                                    .setProgress(0, 0, false);
                            notificationManager.notify(notificationId, builder.build());
                        }

                        Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("Upload error", e.getMessage(), e);
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void onAddButtonClicked() {
        setVisibility(isFabOpen);
        setAnimation(isFabOpen);
        setClickable(isFabOpen);
        isFabOpen = !isFabOpen;
    }

    private void setClickable(boolean isFabOpen) {
        if (!isFabOpen) {
            videoFab.setClickable(true);
            audioFab.setClickable(true);
            imageFab.setClickable(true);
            documentFab.setClickable(true);
        } else {
            videoFab.setClickable(false);
            audioFab.setClickable(false);
            imageFab.setClickable(false);
            documentFab.setClickable(false);
        }
    }

    private void setAnimation(boolean isFabOpen) {
        if (!isFabOpen) {
            videoFab.startAnimation(fromRightToLeft);
            audioFab.startAnimation(fromRightToLeft);
            imageFab.startAnimation(fromRightToLeft);
            documentFab.startAnimation(fromRightToLeft);
            fab.startAnimation(rotateOpen);
        } else {
            videoFab.startAnimation(fromLeftToRight);
            audioFab.startAnimation(fromLeftToRight);
            imageFab.startAnimation(fromLeftToRight);
            documentFab.startAnimation(fromLeftToRight);
            fab.startAnimation(rotateClose);
        }
    }

    private void setVisibility(boolean isFabOpen) {
        if (!isFabOpen) {
            videoFab.setVisibility(android.view.View.VISIBLE);
            audioFab.setVisibility(android.view.View.VISIBLE);
            imageFab.setVisibility(android.view.View.VISIBLE);
            documentFab.setVisibility(android.view.View.VISIBLE);
        } else {
            videoFab.setVisibility(android.view.View.INVISIBLE);
            audioFab.setVisibility(android.view.View.INVISIBLE);
            imageFab.setVisibility(android.view.View.INVISIBLE);
            documentFab.setVisibility(android.view.View.INVISIBLE);
        }
    }
}