package pl.edu.pg.eti.segai;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import org.tensorflow.lite.Interpreter;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GoogleDriveHandler googleDriveHandler;
    private Uri imageUri;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            tflite = new Interpreter(loadModelFile());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("tf_lite_optimized_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void uploadPhoto(View view) throws IOException {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = createImageUri();
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        photoActivityResultLauncher.launch(imageCaptureIntent);
    }

    ActivityResultLauncher<Intent> photoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    calculateAndReturnResult();
                } else
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            });


    private void calculateAndReturnResult() {
       // String type = doAiMagic(imageUri);// todo: use this type and fix using it
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("type", randomType());
        intent.putExtra("probability", randomProbability());
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }

    private String doAiMagic(Uri imageUri) { //todo: run this in proper way
        String result = new String();
        tflite.run(imageUri, result);
        return result;
    }


    public void uploadPhotoFromDisc(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setFlags((Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION));
        uploadPhotoFromDiscActivityResultLauncher.launch(intent);
    }


    ActivityResultLauncher<Intent> uploadPhotoFromDiscActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
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
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void switchToDataCollecting(View view) {
        Intent intent = new Intent(this, DataCollectingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Close app?");
        adb.setPositiveButton("YES",
                (dialog, which) -> {
                    finishAffinity(); //TODO: remove this app from recent apps?
                    finish();
                });
        adb.setNegativeButton("NO",
                (dialog, which) -> {
                });
        adb.create().show();
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri createImageUri() throws IOException {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "img_" + timeStamp;
        // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // File image = File.createTempFile(imageFileName, Constants.EXTENSION_PHOTOS, storageDir);

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

    //TODO: delete randomizing methods
    private Double randomProbability() {
        double probability = 0.01 + Math.random() * (99.99 - 0.01);
        return Math.round(probability * 100) / 100.0;
    }

    private String randomType() {
        String[] results = {
                Constants.TRASH_TYPE_BIO,
                Constants.TRASH_TYPE_PLASTIC_METAL,
                Constants.TRASH_TYPE_PAPER,
                Constants.TRASH_TYPE_GLASS,
                Constants.TRASH_TYPE_MIXED};
        Random r = new Random();
        return results[r.nextInt(results.length)];
    }
}