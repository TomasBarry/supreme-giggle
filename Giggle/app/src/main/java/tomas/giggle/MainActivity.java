package tomas.giggle;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;


public class MainActivity extends Activity {


    public static DropboxAPI<AndroidAuthSession> mDBApi;

    private DeviceId deviceId;
    private String deviceIdString;

    public static DatabaseController databaseController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i("MainActivity", "onCreate for MainActivity started");
        connectToDropBox();
        initializeDeviceId();
        initializeKeys();
        initializeTextViews();
        Log.i("MainActivity", "onCreate for MainActivity finished");
    }


    /**
     * Handler to copy text to clipboard on users device. Called when user taps on device Id, public
     * key or private key fields
     *
     * @param v the current view
     */
    public void copyToClipboard(View v) {
        Log.i("copyToClipboard", "User wants to copy to clipboard");
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String data;
        ClipData clip;
        switch (v.getId()) {
            case R.id.device_id:
                data = deviceId.getDeviceIdString();
                break;
            case R.id.public_key:
                data = new KeyGenerator(this).getPublicKeyAsString();
                break;
            case R.id.private_key:
                data = new KeyGenerator(this).getPrivateKeyAsString();
                break;
            default:
                data = "Error";
        }
        clip = ClipData.newPlainText("label", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }


    /**
     * Handler for when user clicks the open user files button. Starts the PersonalFileView activity
     *
     * @param v the current view
     */
    public void openUserFilesView(View v) {
        Log.i("openUserFilesView", "User wants to open their files");
        startActivity(new Intent(this, PersonalFileView.class));
    }


    public void openDownloadFileView(View v) {
        Log.i("openDownloadFileView", "User wants to download a file");
        Intent i = new Intent(this, ServerFileView.class);
        i.putExtra("userWantsTo", "Download");
        startActivity(i);
    }


    public void openUploadFileView(View v) {
        Log.i("openUploadFileView", "User wants to upload a file");
        Intent i = new Intent(this, ServerFileView.class);
        i.putExtra("userWantsTo", "Upload");
        startActivity(i);
    }


    private void connectToDropBox() {
        Log.i("connectToDropBox", "Connecting to DropBox");
        AppKeyPair appKeys = new AppKeyPair(SecretConstants.APP_KEY, SecretConstants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        Log.i("connectToDropBox", "mDBApi created");
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
        Log.i("connectToDropBox", "Connected to DropBox");
    }


    private void initializeDeviceId() {
        Log.i("initializeDeviceId", "Initializing device id");
        this.deviceId = new DeviceId(this);
        this.deviceIdString = deviceId.getDeviceIdString();
        Log.i("initializeDeviceId", "Initialized device id");
    }


    private void initializeKeys() {
        Log.i("initializeKeys", "Initializing keys");
        KeyGenerator keyGenerator = new KeyGenerator(this);

        Log.i("initializeKeys", "Do keys exist = " + (keyGenerator.getPublicKeyAsString() == null));
        Log.i("initializeKeys", "Key is {" + keyGenerator.getPublicKeyAsString() + "}");
        if (keyGenerator.getPublicKeyAsString().equals("") || keyGenerator.getPublicKeyAsString() == null) {
            keyGenerator.generateKeys();
            Log.i("initializeKeys", "New keys generated");
        }
        Log.i("initializeKeys", "Initialized keys");
    }


    private void initializeTextViews() {
        Log.i("initializeTextViews", "Initializing text views");
        Resources res = getResources();

        KeyGenerator kg = new KeyGenerator(this);

        TextView deviceIdTextView = (TextView) findViewById(R.id.device_id);
        TextView publicKeyTextView = (TextView) findViewById(R.id.public_key);
        TextView privateKeyTextView = (TextView) findViewById(R.id.private_key);

        deviceIdTextView.setText(res.getString(R.string.info_device_id,
                deviceIdString.substring(0, 20)));
        publicKeyTextView.setText(res.getString(R.string.info_public_key,
                kg.getPublicKeyAsString().substring(0, 20)));
        privateKeyTextView.setText(res.getString(R.string.info_private_key,
                kg.getPrivateKeyAsString().substring(0, 20)));
        Log.i("initializeTextViews", "Initialized text views");
    }


    protected void onResume() {
        super.onResume();
        Log.i("onResume_MA", "Resuming activity");
        Log.i("onResume_MA", "" + mDBApi.getSession().authenticationSuccessful());
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();
                databaseController = new DatabaseController(this);
                Log.i("onResume_MA", "databaseControllerObject created");
                if (!databaseController.checkIfValidUser(
                        deviceIdString, new KeyGenerator(this).getPublicKeyAsString())) {
                    Log.i("onResume_MA", "User is not a valid user");
                    finish();
                }
            } catch (Exception e) {
                Log.e("onResume_MA", e.toString(), e);
            }
        }
    }
}