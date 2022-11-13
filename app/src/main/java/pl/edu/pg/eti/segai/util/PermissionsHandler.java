package pl.edu.pg.eti.segai.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsHandler {

    Context context;
    Activity activity;

    public PermissionsHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void permissionToast(@NonNull int[] grantResults, String permissionType) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, permissionType + " Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, permissionType + " Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean permissionNotGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String cameraPermission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{cameraPermission}, requestCode);
    }
}
