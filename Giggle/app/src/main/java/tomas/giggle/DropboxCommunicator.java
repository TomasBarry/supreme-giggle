package tomas.giggle;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileInputStream;

public class DropboxCommunicator {

    private Context context;

    public DropboxCommunicator(Context context) {
        this.context = context;
    }

    public void uploadFile(String path) {
        final String filePath = path;
        Log.i("uploadFile", "Uploading file at " + filePath);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File f = new File(filePath);
                    FileInputStream inputStream = new FileInputStream(f);
                    DropboxAPI.Entry response = MainActivity.mDBApi.putFile(f.getName(), inputStream,
                            f.length(), null, null);
                    Log.i("uploadFile", "The uploaded file's rev is: " + response.rev);
                } catch (Exception e) {
                    Log.e("uploadFile", e.toString(), e);
                }
                Log.i("AsyncTask", "File added :)");
            }
        });
    }

    public void downloadFile(String fileName) {

    }

    public String getRealPathFromURI(Uri uri) {
        Log.i("getRealPathFromURI", "About to get real path");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        Log.i("getRealPathFromURI", "Real path is " + cursor.getString(idx));
        return cursor.getString(idx);
    }
}
