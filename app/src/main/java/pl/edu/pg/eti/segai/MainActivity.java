package pl.edu.pg.eti.segai;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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
}