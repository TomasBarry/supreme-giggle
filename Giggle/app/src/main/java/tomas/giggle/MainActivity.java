package tomas.giggle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.security.PrivateKey;
import java.security.PublicKey;

public class MainActivity extends Activity {

    public static DropboxAPI<AndroidAuthSession> mDBApi;

    private TextView deviceIdTextView;
    private TextView publicKeyTextView;
    private TextView privateKeyTextView;

    private Button goToUserFilesButton;
    private Button uploadButton;
    private Button downloadButton;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String publicKeyAsString;
    private String privateKeyAsString;

    private DeviceId deviceId;
    private String deviceIdString;

    public DatabaseController databaseController;

    private final int CON_TO_DROPBOX = 1000;

    private final static String APP_KEY = SecretConstants.APP_KEY;
    private final static String APP_SECRET = SecretConstants.APP_SECRET;
    private String accessToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i("MainActivity", "onCreate for MainActivity started");
        connectToDropBox();
        initializeDeviceId();
        initializeKeys();
        initializeTextViews();
        initializeButtons();
        Log.i("MainActivity", "onCreate for MainActivity finished");
    }

    private void waitToGetBack(String s) {
        Log.i("waitToGetBack", "Waiting to get file back from DropBox");
        try {
            while (s == null) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            Log.e("waitToGetBack", e.toString(), e);
        }
        Log.i("waitToGetBack", "Got file back from DropBox");
    }


    public void openUserFilesView(View v) {
        Log.i("openUserFilesView", "User wants to open their files");
    }


    public void openDownloadFileView(View v) {
        Log.i("openDownloadFileView", "User wants to download a file");
    }


    public void openUploadFileView(View v) {
        Log.i("openUploadFileView", "User wants to upload a file");
    }


    private void connectToDropBox() {
        Log.i("connectToDropBox", "Connecting to DropBox");
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
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

        if (keyGenerator.getPublicKeyAsString() == null) {
            keyGenerator.generateKeys();
            Log.i("initializeKeys", "New keys generated");
        }

        this.publicKey = keyGenerator.getPublicKey();
        this.privateKey = keyGenerator.getPrivateKey();
        this.publicKeyAsString = keyGenerator.getPublicKeyAsString();
        this.privateKeyAsString = keyGenerator.getPrivateKeyAsString();
        Log.i("initializeKeys", "Initialized keys");
    }


    private void initializeTextViews() {
        Log.i("pk length", "public: " + publicKeyAsString.length());
        Log.i("pk length", "private: " + privateKeyAsString.length());

        Log.i("pk ", publicKeyAsString);
        Log.i("de ", deviceIdString);

        Log.i("initializeTextViews", "Initializing text views");
        Resources res = getResources();

        this.deviceIdTextView = (TextView) findViewById(R.id.device_id);
        this.publicKeyTextView = (TextView) findViewById(R.id.public_key);
        this.privateKeyTextView = (TextView) findViewById(R.id.private_key);

        deviceIdTextView.setText(res.getString(R.string.info_device_id,
                deviceIdString.substring(0, 10)));
        publicKeyTextView.setText(res.getString(R.string.info_public_key,
                publicKeyAsString.substring(0, 10)));
        privateKeyTextView.setText(res.getString(R.string.info_private_key,
                privateKeyAsString.substring(0, 10)));
        Log.i("initializeTextViews", "Initialized text views");
    }


    private void initializeButtons() {
        Log.i("initializeButtons", "Initializing buttons");
        this.goToUserFilesButton = (Button) findViewById(R.id.personal_files_button);
        this.uploadButton = (Button) findViewById(R.id.upload_button);
        this.downloadButton = (Button) findViewById(R.id.download_button);
        Log.i("initializeButtons", "Initialized buttons");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityResult", "Activity result occurred");

    }


    protected void onResume() {
        super.onResume();
        Log.i("onResumeMA", "Resuming activity");
        Log.i("onResumeMA", "" + mDBApi.getSession().authenticationSuccessful());
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();
                this.accessToken = mDBApi.getSession().getOAuth2AccessToken();
                this.databaseController = new DatabaseController(this);
                Log.i("onResumeMA", "databaseControllerObject created");
                if (!databaseController.checkIfValidUser(deviceIdString, publicKeyAsString)) {
                    finish();
                }
            } catch (IllegalStateException e) {
                Log.e("onResumeMA", e.toString(), e);
            }
        }
    }
}