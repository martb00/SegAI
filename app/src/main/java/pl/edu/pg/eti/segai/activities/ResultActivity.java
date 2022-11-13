package pl.edu.pg.eti.segai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import pl.edu.pg.eti.segai.R;
import pl.edu.pg.eti.segai.aiClassification.ClassificationResult;
import pl.edu.pg.eti.segai.googleDrive.UploadToDriveWorker;

public class ResultActivity extends AppCompatActivity {

    TextView resultInfo;
    ClassificationResult classificationResult;
    Uri imageUri;
    ImageView imageView;
    boolean sendResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendResult = false;
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_result);
        resultInfo = findViewById(R.id.textViewResult);
        imageView = findViewById(R.id.imageView);
        resultInfo.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            classificationResult = (ClassificationResult) extras.get("classificationResult");
            imageUri = Uri.parse(extras.getString("imageUri"));
        }
        imageView.setImageURI(imageUri);
        (new Handler()).postDelayed(this::showResultInfo, 1000);
    }

    private void showResultInfo() {
        showResultInfo(classificationResult);
    }

    private void showResultInfo(ClassificationResult classificationResult) {
        resultInfo.setText(String.format("Your trash is %s \nat %s%%",
                classificationResult.getType(),
                classificationResult.getProbability()));
        resultInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        backToMainActivity(null);
    }

    public void backToMainActivity(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Can we save your photo to improve our model?");
        adb.setPositiveButton("YES",
                (dialog, which) -> {
                    sendResult = true;
                    doUploadToDriveWork();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                });
        adb.setNegativeButton("NO",
                (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                });
        adb.create().show();
    }

    private void doUploadToDriveWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = createInputDataForDriveUpload();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(UploadToDriveWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag("segAiDriveTag")
                .build();
        MainActivity.myWorkManager.enqueue(oneTimeWorkRequest);
    }

    private Data createInputDataForDriveUpload() {
        Data.Builder builder = new Data.Builder();
        builder.putString("type", classificationResult.getType());
        builder.putString("imageUri", String.valueOf(imageUri));
        return builder.build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendResult)
            Toast.makeText(getApplicationContext(), "Image is being uploaded, thanks!", Toast.LENGTH_SHORT).show();
    }
}