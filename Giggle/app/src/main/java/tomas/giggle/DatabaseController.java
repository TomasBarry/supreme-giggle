package tomas.giggle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;

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
        } catch (Exception e) {
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

    public Hashtable<String, Boolean> getUsersAndPermissionsFor(String ownerPublicKey, String fileName) {
        Log.i("getUsersAndPermsFor", "Getting user and perms for " + fileName);

        Hashtable<String, Boolean> usersWithPerms = new Hashtable<>();

        Cursor resultSet = database.rawQuery(
                "SELECT UserPublicKey, UserName FROM UserAccounts WHERE UserPublicKey != '" + ownerPublicKey + "'", null);

        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            usersWithPerms.put(resultSet.getString(0), false);
            resultSet.moveToNext();
        }
        Log.i("getUsersAndPermsFor", "Have user public key and perms");

        resultSet = database.rawQuery(
                "SELECT DISTINCT UserPublicKey FROM FileKeys " +
                        "WHERE UserPublicKey != '" + ownerPublicKey + "' " +
                        "AND isOwner = 0 " +
                        "AND File = '" + fileName + "'", null);

        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            usersWithPerms.put(resultSet.getString(0), true);
            resultSet.moveToNext();
        }

        Log.i("getUsersAndPermsFor", "Have user public key and perms updated with trues");

        resultSet = database.rawQuery(
                "SELECT UserPublicKey, UserName FROM UserAccounts WHERE UserPublicKey != '" + ownerPublicKey + "'", null);

        Hashtable<String, Boolean> userNamesWithPerms = new Hashtable<>();
        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            userNamesWithPerms.put(resultSet.getString(1), usersWithPerms.get(resultSet.getString(0)));
            resultSet.moveToNext();
        }

        Log.i("getUsersAndPermsFor", "Have user names and perms");
        return userNamesWithPerms;
    }

    public String[] getFilesFor(String userPublicKey) {
        Log.i("getFilesFor", "Getting files for " + userPublicKey);
        Cursor resultSet = database.rawQuery(
                "SELECT DISTINCT File FROM FileKeys WHERE UserPublicKey = '" + userPublicKey + "' AND isOwner = 1", null);
        String[] fileNames = new String[resultSet.getCount()];
        resultSet.moveToFirst();
        int i = 0;
        while (!resultSet.isAfterLast()) {
            Log.i("getFilesFor", "File namme: " + resultSet.getString(0));
            fileNames[i++] = resultSet.getString(0);
            resultSet.moveToNext();
        }
        return fileNames;
    }

    public boolean checkIfValidUser(String deviceId, String publicKey) {
        Log.i("checkIfValidUser", "Checking if " + deviceId + " is a valid user");
        Cursor resultSet = database.rawQuery(
                "SELECT UserPublicKey, UniqueDeviceId FROM UserAccounts", null);
        // make sure that there are resulting rows
        if (resultSet.getCount() > 0) {
            String receivedPublicKey;
            String receivedDeviceId;
            resultSet.moveToFirst();
            while (!resultSet.isAfterLast()) {
                receivedPublicKey = resultSet.getString(0);
                receivedDeviceId = resultSet.getString(1);
                // if the deviceId is already in the table then return true
                if (receivedDeviceId.equals(deviceId)
                        && receivedPublicKey.equals(publicKey)) {
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
