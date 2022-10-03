package pl.edu.pg.eti.segai;

import static java.lang.Character.toChars;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ResultActivity extends AppCompatActivity {

    TextView resultInfo;
    String type;
    Double probability;
    ImageView imageView;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultInfo = findViewById(R.id.textViewResult);
        Button backButton = findViewById(R.id.backButton);
        backButton.setText(new String(toChars(0x1F519)));
        imageView = findViewById(R.id.imageView);
        resultInfo.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            probability = extras.getDouble("probability");
            imageUri = Uri.parse(extras.getString("imageUri"));
        }
        imageView.setImageURI(imageUri);

        (new Handler()).postDelayed(this::showResultInfo, 1000);
    }

    private void showResultInfo() {
        showResultInfo(type, probability);
    }

    private void showResultInfo(String type, Double probability) {
        resultInfo.setText(String.format("Your trash is %s at %s%%", type, probability));
        resultInfo.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        backToMainActivity(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void backToMainActivity(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Can we save your photo to improve our model?");
        adb.setPositiveButton("YES",
                (dialog, which) -> {
                    uploadToDrive();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                });
        adb.setNegativeButton("NO",
                (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                });
        adb.create().show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadToDrive() {
        try {
            GoogleDriveHandler googleDriveHandler = new GoogleDriveHandler(ResultActivity.this);
            googleDriveHandler.uploadFile(imageUri, type, true); // todo: change folder ?
            Toast.makeText(ResultActivity.this, "Image sent, thanks!", Toast.LENGTH_SHORT).show();

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}