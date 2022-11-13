package pl.edu.pg.eti.segai.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import java.io.IOException;

import pl.edu.pg.eti.segai.R;
import pl.edu.pg.eti.segai.aiClassification.AiClassifier;
import pl.edu.pg.eti.segai.aiClassification.ClassificationResult;
import pl.edu.pg.eti.segai.util.ImageUriCreator;
import pl.edu.pg.eti.segai.util.PermissionsHandler;

public class MainActivity extends AppCompatActivity {

    private static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_CODE = 0;
    private static final int STORAGE_CODE = 1;

    private static final int IMAGE_SIZE = 224;
    private Uri imageUri;
    public static WorkManager myWorkManager;
    private AiClassifier aiClassifier;
    private PermissionsHandler permissionsHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWorkManager = WorkManager.getInstance(getApplicationContext());
        aiClassifier = new AiClassifier();
        permissionsHandler = new PermissionsHandler(getApplicationContext(), this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("imageUri", String.valueOf(imageUri));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUri = Uri.parse(savedInstanceState.getString("imageUri", null));
    }

    public void uploadPhoto(View view) {
        if (permissionsHandler.permissionNotGranted(CAMERA_PERMISSION)) {
            permissionsHandler.requestPermission(CAMERA_PERMISSION, CAMERA_CODE);
        } else {
            if (permissionsHandler.permissionNotGranted(STORAGE_PERMISSION)) {
                permissionsHandler.requestPermission(STORAGE_PERMISSION, STORAGE_CODE);
            } else {
                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = ImageUriCreator.createImageUri(getContentResolver());
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                photoFromCameraActivityResultLauncher.launch(imageCaptureIntent);
            }
        }
    }

    ActivityResultLauncher<Intent> photoFromCameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    calculateAndReturnResult();
                } else
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            });


    @RequiresApi(api = Build.VERSION_CODES.R)
    public void uploadPhotoFromDisc(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setFlags((Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION));
        uploadPhotoFromDiscActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> uploadPhotoFromDiscActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data != null ? data.getData() : null;
                    if (imageUri != null && imageUri.toString().contains("image"))
                        calculateAndReturnResult();
                    else
                        Toast.makeText(MainActivity.this, "Failed. You didn't choose an image!", Toast.LENGTH_SHORT).show();
                } else {
                    imageUri = null;
                    Toast.makeText(MainActivity.this, "Failed. You didn't choose any file!", Toast.LENGTH_SHORT).show();
                }
            });

    private void calculateAndReturnResult() {
        try {
            Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            image = Bitmap.createScaledBitmap(image, IMAGE_SIZE, IMAGE_SIZE, false);
            ClassificationResult classificationResult = aiClassifier.classifyImage(image, IMAGE_SIZE, getApplicationContext());

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("classificationResult", classificationResult);
            intent.putExtra("imageUri", imageUri.toString());
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Close app?");
        adb.setPositiveButton("YES",
                (dialog, which) -> {
                    finishAffinity();
                    finish();
                });
        adb.setNegativeButton("NO",
                (dialog, which) -> {
                });
        adb.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            permissionsHandler.permissionToast(grantResults, "Camera Permission");
        }
        if (requestCode == STORAGE_CODE) {
            permissionsHandler.permissionToast(grantResults, "Storage Permission");
        }
    }
}