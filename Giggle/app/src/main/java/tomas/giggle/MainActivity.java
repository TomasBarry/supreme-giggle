package tomas.giggle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static DropboxAPI<AndroidAuthSession> mDBApi;

    // Buttons in the Main Activity
    private static Button encryptButton;
    private static Button decryptButton;

    // Text fields in the Main Activity
    private static TextView uniqueDeviceId;
    private static TextView publicKeyText;
    private static TextView privateKeyText;

    private String UNIQUE_DEVICE_ID;

    // Database for cloud storage encryption
    private static SQLiteDatabase DATA_BASE;

    public static final int REQUEST_CODE_CON_TO_DROPBOX = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UNIQUE_DEVICE_ID = getUniqueAndroidDeviceId();
        instantiateButtons();
        instantiateTextViews();

        startActivityForResult(
                new Intent(this, ConnectToDropBoxActivity.class), REQUEST_CODE_CON_TO_DROPBOX);
        Log.d("Giggle_onCreate", "Init finished");
    }


    /**
     * instantiateButtons
     * <p/>
     * Connect to and set up listeners and handlers for buttons in the Main Activity
     */
    public void instantiateButtons() {
        encryptButton = (Button) findViewById(R.id.encrypt_button);
        decryptButton = (Button) findViewById(R.id.decrpyt_button);

        assert encryptButton != null;
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        assert decryptButton != null;
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        Log.d("instantiateButtons", "Buttons and listeners created");
    }


    /**
     * instantiateTextViews
     * <p/>
     * Connect and populate text views for the Main Activity
     */
    public void instantiateTextViews() {
        TextView uniqueDeviceId = (TextView) findViewById(R.id.unique_device_id);
        TextView publicKeyText = (TextView) findViewById(R.id.public_key_value);
        TextView privateKeyText = (TextView) findViewById(R.id.private_key_value);

        assert uniqueDeviceId != null;
        Resources res = getResources();
        String text = res.getString(R.string.device_id_string, UNIQUE_DEVICE_ID);
        uniqueDeviceId.setText(text);
        Log.d("instantiateTextViews", "Text views created");
    }


    /**
     * getUniqueAndroidDeviceId
     * <p/>
     * Calculate the unique device ID and return it
     *
     * @return a String representing a unique ID for the device
     */
    public String getUniqueAndroidDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("getUniqueAndroidDevice", "tm created");
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Log.d("getUniqueAndroidDevice", "strings created");

        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        Log.d("Giggle_getUniqueAndroid", "UUID created");
        return deviceUuid.toString();
    }


    /**
     * addFileToDropBox
     * <p/>
     * Add a file to DropBox. Files are added to the root folder for the moment.
     * Note: Run this method in a background task
     *
     * @param file: the file to add to DropBox
     */
    public void addFileToDropBox(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            DropboxAPI.Entry response = mDBApi.putFile(file.getName(), inputStream,
                    file.length(), null, null);
            Log.d("addFileToDropBox", "The uploaded file's rev is: " + response.rev);
        } catch (Exception e) {
            Log.e("addFileToDropBox", "IOException", e);
        }
    }


    /**
     * getAppDatabase
     * <p/>
     * Download the most up to date version of the Securing The Cloud database.
     * Note: Run this method in a background task
     *
     * @return a file object containing the updated database
     */
    public File getAppDatabase() {
        try {
            File file = File.createTempFile(SecretConstants.DATABASE_NAME, "");
            Log.d("getAppDatabase", "File name: " + file.getName());
            String fileName =
                    file.getName().substring(0, SecretConstants.DATABASE_NAME.length() - 1);
            file.deleteOnExit();
            Log.d("getAppDatabase", "File name: " + fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info =
                    mDBApi.getFile(fileName, null, outputStream, null);
            Log.d("getAppDatabase", "The file's rev is: " + info.getMetadata().rev);
            return file;
        } catch (Exception e) {
            Log.e("getAppDatabase", e.toString(), e);
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CON_TO_DROPBOX:
                Log.d("Giggle_onActivityResult", "" + mDBApi.getSession().authenticationSuccessful());
                Log.d("Giggle_onActivityResult", mDBApi.getSession().getOAuth2AccessToken());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        File file = getAppDatabase();
                        Log.d("Giggle_onActivityResult", "Name: " + file.getName());
                        Log.d("Giggle_onActivityResult", "Path: " + file.getPath());
                    }
                });
        }
    }
}
