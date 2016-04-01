package tomas.giggle;

import android.content.ContentValues;
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
    private boolean finished = false;

    public DatabaseController(final Context con) throws InterruptedException {
        Log.i("DatabaseController", "Constructor begins");
        this.context = con;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                databaseFile = getAppDatabaseFile();
                Log.i("AsyncTask", "database file received");
                database = context.openOrCreateDatabase(databaseFile.getPath(), context.MODE_PRIVATE, null);
                finished = true;
            }
        });
        customWait();
        Log.i("DatabaseController", "Constructor ends");
    }

    private void customWait() throws InterruptedException {
        while (!finished) {
            Thread.sleep(1);
        }
        finished = false;
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

        resultSet.close();
        Log.i("getUsersAndPermsFor", "Have user names and perms");
        return userNamesWithPerms;
    }

    public String[] getFilesOnServer() {
        Log.i("getFilesFor", "Getting all files on server available to "
                + new KeyGenerator(context).getPublicKeyAsString());
        Cursor resultSet = database.rawQuery(
                "SELECT DISTINCT File FROM FileKeys WHERE UserPublicKey = " +
                        "'" + new KeyGenerator(context).getPublicKeyAsString() + "'", null);
        String[] fileNames = new String[resultSet.getCount()];
        resultSet.moveToFirst();
        int i = 0;
        while (!resultSet.isAfterLast()) {
            Log.i("getFilesFor", "File namme: " + resultSet.getString(0));
            fileNames[i++] = resultSet.getString(0);
            resultSet.moveToNext();
        }
        resultSet.close();
        return fileNames;
    }

    public String[] getFilesFor(String userPublicKey) {
        Log.i("getFilesFor", "Getting files for " + userPublicKey);
        Cursor resultSet = database.rawQuery("SELECT DISTINCT File FROM FileKeys " +
                "WHERE UserPublicKey = '" + userPublicKey + "' AND isOwner = 1", null);
        String[] fileNames = new String[resultSet.getCount()];
        resultSet.moveToFirst();
        int i = 0;
        while (!resultSet.isAfterLast()) {
            Log.i("getFilesFor", "File namme: " + resultSet.getString(0));
            fileNames[i++] = resultSet.getString(0);
            resultSet.moveToNext();
        }
        resultSet.close();
        return fileNames;
    }

    public void addFileEntryIntoFileKeys(
            String encKey, String publicKey, String fileName, int isOwner)
            throws InterruptedException {
        Log.i("addFileEntryIntoFK", "About to add entry with encKey " + encKey + "," +
                " publicKey " + publicKey + " fileName " + fileName + " isOwner " + isOwner);
        ContentValues contentValues = new ContentValues();
        contentValues.put("EncKey", encKey);
        contentValues.put("UserPublicKey", publicKey);
        contentValues.put("File", fileName);
        contentValues.put("isOwner", isOwner);
        database.insert("FileKeys", null, contentValues);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateDatabaseFile();
                Log.i("AsyncTask", "database file updated");
                finished = true;
            }
        });
        customWait();
        Log.i("addFileEntryIntoFK", "Added entry");
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
        Cursor resultSet = database.rawQuery(
                "SELECT UserPublicKey FROM UserAccounts WHERE UserName = '" + userName + "'", null);
        resultSet.moveToFirst();
        String retString = resultSet.getString(0);
        resultSet.close();
        return retString;
    }

    public void revokePermissionsFor(String userName, String fileName) throws InterruptedException {
        Log.i("revokePermissionsFor",
                "About to revoke permissions for " + userName + " on file " + fileName);
        String userPublicKey = getUserKeyFromUserName(userName);
        Log.i("revokePermissionsFor", "About to revoke permissions for " + userPublicKey);
        database.delete("FileKeys", "UserPublicKey = ? AND File = ?", new String[]{
                userPublicKey, fileName
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateDatabaseFile();
                Log.i("AsyncTask", "database file updated");
                finished = true;
            }
        });
        customWait();
    }

    public void addPermissionFor(String userName, String fileName) throws InterruptedException {
        Log.i("addPermissionFor",
                "About to add permissions for " + userName + " on file " + fileName);
        String userPublicKey = getUserKeyFromUserName(userName);

        EncryptionKey encryptionKeyDecrypted =
                new EncryptionKey(MainActivity.databaseController.getEncKeyFor(fileName),
                        userPublicKey, true, context);

        EncryptionKey encryptionKeyEncrypted = new EncryptionKey(
                encryptionKeyDecrypted.getDecryptedKey(), userPublicKey, false, context);

        ContentValues contentValues = new ContentValues();
        contentValues.put("EncKey", encryptionKeyEncrypted.getEncryptedKey());
        contentValues.put("UserPublicKey", userPublicKey);
        contentValues.put("File", fileName);
        contentValues.put("isOwner", 0);
        database.insert("FileKeys", null, contentValues);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateDatabaseFile();
                Log.i("AsyncTask", "database file updated");
                finished = true;
            }
        });
        customWait();
    }

    public String getEncKeyFor(String fileName) {
        Log.i("getEncKeyFor", "Begining to get EncKey for file " + fileName);
        Cursor resultSet =
                database.rawQuery("SELECT EncKey FROM FileKeys " +
                        "WHERE UserPublicKey = '" + new KeyGenerator(context).getPublicKeyAsString() + "' " +
                        "AND File = '" + fileName + "'", null);
        resultSet.moveToFirst();
        Log.i("getEncKeyFor", "EncKey is " + resultSet.getString(0));
        String retString = resultSet.getString(0);
        resultSet.close();
        return retString;
    }
}
