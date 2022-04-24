package pl.edu.pg.eti.segai;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class MainActivity extends AppCompatActivity {

    private GoogleDriveHandler googleDriveHandler;
    private Uri imageUri;
    private String trashType;
    private AlertDialog.Builder builderTrashTypeForCamera;
    private AlertDialog.Builder builderTrashTypeForDisc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        createTrashTypeDialogForCamera();
        createTrashTypeDialogForDisc();
    }

    private void createTrashTypeDialogForCamera() {
        builderTrashTypeForCamera = new AlertDialog.Builder(this);
        builderTrashTypeForCamera.setTitle("Select trash type:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(Constants.TRASH_TYPE_BIO);
        arrayAdapter.add(Constants.TRASH_TYPE_PLASTIC_METAL);
        arrayAdapter.add(Constants.TRASH_TYPE_PAPER);
        arrayAdapter.add(Constants.TRASH_TYPE_GLASS);
        arrayAdapter.add(Constants.TRASH_TYPE_MIXED);

        builderTrashTypeForCamera.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderTrashTypeForCamera.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                trashType = arrayAdapter.getItem(which);
                try {
                    Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = createImageUri();
                    imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    photoActivityResultLauncher.launch(imageCaptureIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createTrashTypeDialogForDisc() {
        builderTrashTypeForDisc = new AlertDialog.Builder(this);
        builderTrashTypeForDisc.setTitle("Select trash type:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(Constants.TRASH_TYPE_BIO);
        arrayAdapter.add(Constants.TRASH_TYPE_PLASTIC_METAL);
        arrayAdapter.add(Constants.TRASH_TYPE_PAPER);
        arrayAdapter.add(Constants.TRASH_TYPE_GLASS);
        arrayAdapter.add(Constants.TRASH_TYPE_MIXED);

        builderTrashTypeForDisc.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderTrashTypeForDisc.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                trashType = arrayAdapter.getItem(which);
                uploadToDrive();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadToDrive() {
        try {
            googleDriveHandler = new GoogleDriveHandler(MainActivity.this);
            googleDriveHandler.uploadFile(imageUri, trashType);
            Toast.makeText(MainActivity.this, "Image sent!", Toast.LENGTH_SHORT).show();

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public void uploadPhoto(View view) {
        builderTrashTypeForCamera.show();
    }

    public void uploadPhotoFromDisc(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setFlags((Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION));
        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == Activity.RESULT_OK) {
                    imageUri = data.getData();
                    if (imageUri.toString().contains("image"))
                        builderTrashTypeForDisc.show();
                    else
                        Toast.makeText(MainActivity.this, "Failed. You didn't choose an image!", Toast.LENGTH_SHORT).show();
                } else {
                    imageUri = null;
                    Toast.makeText(MainActivity.this, "Failed. You didn't choose any file!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    ActivityResultLauncher<Intent> photoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        uploadToDrive();
                    } else
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

    private Uri createImageUri() throws IOException {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "img_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, Constants.EXTENSION_PHOTOS, storageDir);

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            uri = MediaStore.Images.Media.getContentUri((MediaStore.VOLUME_EXTERNAL_PRIMARY));
        else
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName + Constants.EXTENSION_PHOTOS);
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Constants.PATH_PHOTOS);
        return resolver.insert(uri, contentValues);
    }


    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 0);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Close app?");
        adb.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                });
        adb.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        adb.create().show();
    }
}

