package pl.edu.pg.eti.segai.googleDrive;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class UploadToDriveWorker extends Worker {

    public UploadToDriveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        String type = getInputData().getString("type");
        Uri imageUri = Uri.parse(getInputData().getString("imageUri"));
        uploadToDrive(imageUri, type);
        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadToDrive(Uri imageUri, String type) {
        try {
            GoogleDriveHandler googleDriveHandler = new GoogleDriveHandler(getApplicationContext());
            googleDriveHandler.uploadFile(imageUri, type, true);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
