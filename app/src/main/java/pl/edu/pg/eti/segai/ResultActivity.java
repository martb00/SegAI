package pl.edu.pg.eti.segai;

import static java.lang.Character.toChars;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    TextView resultInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        resultInfo = findViewById(R.id.textViewResult);
        Button backButton = findViewById(R.id.backButton);
        backButton.setText(new String(toChars(0x1F519)));
        resultInfo.setVisibility(View.INVISIBLE);
        (new Handler()).postDelayed(this::showRandomResultInfo, 1000);
    }

    private void showRandomResultInfo() {
        showResultInfo(randomType(), randomProbability());
    }


    private void showResultInfo(String type, Double probability) {
        resultInfo.setText(String.format("Your trash is %s at %s%%", type, probability));
        resultInfo.setVisibility(View.VISIBLE);
    }

    private Double randomProbability() {
        double probability = 0.01 + Math.random() * (99.99 - 0.01);
        return Math.round(probability * 100) / 100.0;
    }

    private String randomType() {
        String[] results = {
                "BIO",
                "PLASTIC/METAL",
                "PAPER",
                "GLASS",
                "MIXED"};
        Random r = new Random();
        return results[r.nextInt(results.length)];
    }

    @Override
    public void onBackPressed() {
        backToMainActivity(null);
    }

    public void backToMainActivity(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Can we save your photo to improve our model?");
        adb.setPositiveButton("YES",
                (dialog, which) -> {
                    //TODO: SAVE PHOTO with result!!!
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    //TODO: maybe add some "thank you" pop up
                });
        adb.setNegativeButton("NO",
                (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                });
        adb.create().show();
    }
}