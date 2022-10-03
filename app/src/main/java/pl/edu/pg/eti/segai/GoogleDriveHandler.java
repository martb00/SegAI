package pl.edu.pg.eti.segai;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class GoogleDriveHandler {

    private final String APPLICATION_NAME = Constants.APPLICATION_NAME;
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private final String CREDENTIALS_FILE_PATH = Constants.PATH_GOOGLE_DRIVE_CREDENTIALS;
    private Drive service;
    private Context context;

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleDriveHandler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null)
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        return GoogleCredential.fromStream(in).createScoped(SCOPES);
    }

    private String getDriveFolderIdCollectingMode(String trashType) {
        switch (trashType) {
            case Constants.TRASH_TYPE_BIO:
                return Constants.DRIVE_BIO_COLLECTING_MODE;
            case Constants.TRASH_TYPE_MIXED:
                return Constants.DRIVE_MIXED_COLLECTING_MODE;
            case Constants.TRASH_TYPE_PAPER:
                return Constants.DRIVE_PAPER_COLLECTING_MODE;
            case Constants.TRASH_TYPE_PLASTIC_METAL:
                return Constants.DRIVE_PLASTIC_METAL_COLLECTING_MODE;
            case Constants.TRASH_TYPE_GLASS:
                return Constants.DRIVE_GLASS_COLLECTING_MODE;
            default:
                return Constants.DRIVE_ROOT;
        }
    }
    private String getDriveFolderIdForResult(String trashType) {
        switch (trashType) {
            case Constants.TRASH_TYPE_BIO:
                return Constants.DRIVE_BIO_RESULT;
            case Constants.TRASH_TYPE_MIXED:
                return Constants.DRIVE_MIXED_RESULT;
            case Constants.TRASH_TYPE_PAPER:
                return Constants.DRIVE_PAPER_RESULT;
            case Constants.TRASH_TYPE_PLASTIC_METAL:
                return Constants.DRIVE_PLASTIC_METAL_RESULT;
            case Constants.TRASH_TYPE_GLASS:
                return Constants.DRIVE_GLASS_RESULT;
            default:
                return Constants.DRIVE_ROOT;
        }
    }

    public GoogleDriveHandler(Context context) throws GeneralSecurityException, IOException {
        this.context = context;
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadFile(Uri imageUri, String trashType, boolean result) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        java.io.File imageFile = new java.io.File(context.getCacheDir(), "temp_file");
        imageFile.createNewFile();
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        Files.copy(inputStream, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        File fileMetadata = new File();
        fileMetadata.setName(UUID.randomUUID().toString());
        if (result) {
            fileMetadata.setParents(Collections.singletonList(getDriveFolderIdForResult(trashType)));
        } else {
            fileMetadata.setParents(Collections.singletonList(getDriveFolderIdCollectingMode(trashType)));
        }
        FileContent mediaContent = new FileContent(Constants.MIME_TYPE_PHOTOS, imageFile);
        service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
    }
}