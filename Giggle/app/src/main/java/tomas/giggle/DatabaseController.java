package tomas.giggle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileOutputStream;

public class DatabaseController {

    private Context context;
    private File databaseFile;
    private SQLiteDatabase database;

    public DatabaseController(Context context) {
        Log.i("DatabaseController", "Constructor begins");
        this.context = context;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                databaseFile = getAppDatabaseFile();
                Log.i("AsyncTask", "database file received");

            }
        });
        waitToGetBackDBFile();
        this.database =
                context.openOrCreateDatabase(databaseFile.getPath(), context.MODE_PRIVATE, null);
        Log.i("DatabaseController", "Constructor ends");
    }


    private void waitToGetBackDBFile() {
        Log.i("waitToGetBack_DBC", "Waiting to get file back from DropBox");
        try {
            while (this.databaseFile == null) {
                Thread.sleep(250);
                Log.i("waitToGetBack_DBC", "Waiting");
            }
        }
        catch (Exception e) {
            Log.e("waitToGetBack_DBC", e.toString(), e);
        }
        Log.i("waitToGetBack_DBC", "Got file back from DropBox");
    }


    /**
     * getAppDatabase
     * <p/>
     * Download the most up to date version of the Securing The Cloud database.
     * Note: Run this method in a background task
     *
     * @return a file object containing the updated database
     */
    private File getAppDatabaseFile() {
        Log.i("getAppDatabase", "Getting app database");
        try {
            File file = new File(this.context.getFilesDir(), SecretConstants.DATABASE_NAME);
            Log.i("getAppDatabase", "File name: " + file.getName());
            FileOutputStream outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info =
                    MainActivity.mDBApi.getFile(file.getName(), null, outputStream, null);
            Log.i("getAppDatabase", "The file's rev is: " + info.getMetadata().rev);
            return file;
        } catch (Exception e) {
            Log.e("getAppDatabase", e.toString(), e);
        }
        return null;
    }

    public boolean checkIfValidUser(String deviceId, String publicKey) {
        Log.i("checkIfValidUser", "Checking if " + deviceId + " is a valid user");
        Cursor resultSet = database.rawQuery(
                "SELECT UniqueDeviceId, UserPublicKey FROM UserAccounts", null);
        // make sure that there are resulting rows
        if (resultSet.getCount() > 0) {
            resultSet.moveToFirst();
            while (!resultSet.isAfterLast()) {
                // if the deviceId is already in the table then return true
                if (resultSet.getString(0).equals(deviceId)
                        && resultSet.getString(1).equals(publicKey)) {
                    Log.i("checkIfValidUser", "User is a valid user");
                    resultSet.close();
                    return true;
                }
                resultSet.moveToNext();
            }
        }
        Log.i("checkIfValidUser", "User is not a valid user");
        return false;
    }

    public File getDatabaseFile() {
        return this.databaseFile;
    }


    public SQLiteDatabase getDatabase() {
        return this.database;
    }
}
