package tomas.giggle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileInputStream;
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

    private void waitToUploadDBFile() {
        Log.i("waitToUploadDBFile", "Waiting to upload file to DropBox");
        try {
            while (this.databaseFile == null) {
                Thread.sleep(250);
                Log.i("waitToUploadDBFile", "Waiting");
            }
        } catch (Exception e) {
            Log.e("waitToUploadDBFile", e.toString(), e);
        }
        Log.i("waitToUploadDBFile", "Uploaded file to DropBox");
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

    private void updateDatabaseFile() {
        Log.i("updateDatabaseFile", "Updating server app database");
        try {
            FileInputStream inputStream = new FileInputStream(databaseFile);
            DropboxAPI.Entry response = MainActivity.mDBApi.putFileOverwrite(databaseFile.getName(), inputStream,
                    databaseFile.length(), null);
            Log.i("updateDatabaseFile", "The uploaded file's rev is: " + response.rev);
        } catch (Exception e) {
            Log.e("updateDatabaseFile", e.toString(), e);
        }
    }

    public Hashtable<String, Boolean> getUsersAndPermissionsFor(String ownerPublicKey, String fileName) {
        Log.i("getUsersAndPermsFor", "Getting user and perms for " + fileName);

        Hashtable<String, Boolean> usersWithPerms = new Hashtable<>();

        Cursor resultSet = database.rawQuery(
                "SELECT UserPublicKey, UserName FROM UserAccounts WHERE UserPublicKey != '" + ownerPublicKey + "'", null);

        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            Log.i("getUsersAndPermsFor", "User: " + resultSet.getString(0) + " is permissioned " + false);
            usersWithPerms.put(resultSet.getString(0), false);
            resultSet.moveToNext();
        }

        resultSet = database.rawQuery(
                "SELECT DISTINCT UserPublicKey FROM FileKeys " +
                        "WHERE UserPublicKey != '" + ownerPublicKey + "' " +
                        "AND isOwner = 0 " +
                        "AND File = '" + fileName + "'", null);

        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            Log.i("getUsersAndPermsFor", "User: " + resultSet.getString(0) + " is permissioned " + true);
            usersWithPerms.put(resultSet.getString(0), true);
            resultSet.moveToNext();
        }

        resultSet = database.rawQuery(
                "SELECT UserPublicKey, UserName FROM UserAccounts WHERE UserPublicKey != '" + ownerPublicKey + "'", null);

        Hashtable<String, Boolean> userNamesWithPerms = new Hashtable<>();
        resultSet.moveToFirst();
        while (!resultSet.isAfterLast()) {
            Log.i("getUsersAndPermsFor", "User Name: " + resultSet.getString(1) + ", permission: " + usersWithPerms.get(resultSet.getString(0)));
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

    public String getUserKeyFromUserName(String userName) {
        Cursor resultSet = database.rawQuery("SELECT UserPublicKey FROM UserAccounts WHERE UserName = '" + userName + "'", null);
        resultSet.moveToFirst();
        return resultSet.getString(0);
    }

    public void revokePermissionsFor(String userName, String fileName) {
        Log.i("revokePermissionsFor", "About to revoke permissions for " + userName + " on file " + fileName);
        String userPublicKey = getUserKeyFromUserName(userName);
        Log.i("revokePermissionsFor", "About to revoke permissions for " + userPublicKey);
        database.execSQL(
                "DELETE FROM FileKeys " +
                        "WHERE UserPublicKey is '" + userPublicKey + "' " +
                        "AND File is '" + fileName + "'");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateDatabaseFile();
                Log.i("AsyncTask", "database file updated");

            }
        });
        waitToUploadDBFile();
    }

    public void addPermissionFor(String userName, String fileName) {
        Log.i("addPermissionFor", "About to add permissions for " + userName + " on file " + fileName);
        String userPublicKey = getUserKeyFromUserName(userName);

        EncryptionKey encryptionKeyDecrypted =
                new EncryptionKey(MainActivity.databaseController.getEncKeyFor(fileName),
                        userPublicKey, true, context);

        Log.i("addPermissionFor", "encryptionKeyDecrypted.encryptedKey " + encryptionKeyDecrypted.getEncryptedKey());
        Log.i("addPermissionFor", "encryptionKeyDecrypted.privateKey " + encryptionKeyDecrypted.getPrivateKey());
        Log.i("addPermissionFor", "encryptionKeyDecrypted.plainKey " + encryptionKeyDecrypted.getDecryptedKey());

        EncryptionKey encryptionKeyEncrypted =
                new EncryptionKey(encryptionKeyDecrypted.getDecryptedKey(), userPublicKey, false, context);

        Log.i("addPermissionFor", "encryptionKeyEncrypted.plainKey " + encryptionKeyEncrypted.getPlainKey());
        Log.i("addPermissionFor", "encryptionKeyEncrypted.publicKey " + encryptionKeyEncrypted.getPublicKey());
        Log.i("addPermissionFor", "encryptionKeyEncrypted.encryptedKey " + encryptionKeyEncrypted.getEncryptedKey());

        database.execSQL("INSERT INTO FileKeys VALUES(" +
                "'" + encryptionKeyEncrypted.getEncryptedKey() + "', " +
                "'" + userPublicKey + "', " +
                "'" + fileName + "', " +
                "0)");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateDatabaseFile();
                Log.i("AsyncTask", "database file updated");

            }
        });
        waitToUploadDBFile();
    }

    public String getEncKeyFor(String fileName) {
        Log.i("getEncKeyFor", "Begining to get EncKey for file " + fileName);
        Cursor resultSet =
                database.rawQuery("SELECT EncKey FROM FileKeys " +
                        "WHERE isOwner = 1 " +
                        "AND File = '" + fileName + "'", null);
        resultSet.moveToFirst();
        Log.i("getEncKeyFor", "EncKey is " + resultSet.getString(0));
        return resultSet.getString(0);
    }

    public File getDatabaseFile() {
        return this.databaseFile;
    }


    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public void printTable(String table) {
        Log.i("printTable", "Entering printTable");
        Cursor resultSet;
        switch (table) {
            case ("UserAccounts"):
                Log.i("printTable", "About to print contents of " + table);
                resultSet = database.rawQuery(
                        "SELECT * FROM UserAccounts", null);
                resultSet.moveToFirst();
                while (!resultSet.isAfterLast()) {
                    Log.i("printTable_" + table,
                            resultSet.getString(0) + " | " + resultSet.getString(1) + " | " + resultSet.getString(2));
                    resultSet.moveToNext();
                }
                resultSet.close();
                Log.i("printTable", "Printed contents of " + table);
                break;

            case ("FileKeys"):
                Log.i("printTable", "About to print contents of " + table);
                resultSet = database.rawQuery(
                        "SELECT * FROM FileKeys", null);
                resultSet.moveToFirst();
                while (!resultSet.isAfterLast()) {
                    Log.i("printTable_" + table,
                            resultSet.getString(0) + " | " + resultSet.getString(1) +
                                    " | " + resultSet.getString(2) + " | " + resultSet.getString(3));
                    resultSet.moveToNext();
                }
                resultSet.close();
                Log.i("printTable", "Printed contents of " + table);
                break;
            default:
                Log.i("printTable", "No table called " + table);

        }
    }
}
