package pl.edu.pg.eti.segai.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class ImageUriCreator {
    public static Uri createImageUri(ContentResolver resolver) {
        long timeStamp = System.currentTimeMillis();
        String imageFileName = "img_" + timeStamp + "_" + Math.random();

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            uri = MediaStore.Images.Media.getContentUri((MediaStore.VOLUME_EXTERNAL_PRIMARY));
        else
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName + Constants.EXTENSION_PHOTOS);
//      contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Constants.PATH_PHOTOS);
        return resolver.insert(uri, contentValues);
    }
}
