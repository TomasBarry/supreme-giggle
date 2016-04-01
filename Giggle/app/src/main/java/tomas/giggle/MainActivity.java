package tomas.giggle;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

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

    public static DatabaseController databaseController;

    private final int CON_TO_DROPBOX = 1000;

    private final static String APP_KEY = SecretConstants.APP_KEY;
    private final static String APP_SECRET = SecretConstants.APP_SECRET;
    private String accessToken = null;

    private boolean hasPermissions;

    private String[] neededPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MANAGE_DOCUMENTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int REQUEST_CODE_ASK_PERMISSIONS = 81;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i("MainActivity", "onCreate for MainActivity started");

//        getPermissions();
//        waitForPermissions();
        connectToDropBox();
        initializeDeviceId();
        initializeKeys();
        initializeTextViews();
        initializeButtons();
        Log.i("MainActivity", "onCreate for MainActivity finished");
    }

    private void waitForPermissions() {
        Log.i("waitForPermissions", "About to wait for permissions");
        try {
            while (!hasPermissions) {
                Thread.sleep(250);
                Log.i("waitForPermissions", "Waiting");
            }
        } catch (Exception e) {
            Log.e("waitForPermissions", e.toString(), e);
        }
        Log.i("waitForPermissions", "Have permissions");
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
        Intent i = new Intent(this, PersonalFileView.class);
        i.putExtra("userPublicKey", publicKeyAsString);
        startActivity(i);
    }


    public void openDownloadFileView(View v) {
        Log.i("openDownloadFileView", "User wants to download a file");
        Intent i = new Intent(this, ServerFileView.class);
        i.putExtra("userPublicKey", publicKeyAsString);
        i.putExtra("userWantsTo", "Download");
        startActivity(i);
    }


    public void openUploadFileView(View v) {
        Log.i("openUploadFileView", "User wants to upload a file");
        Intent i = new Intent(this, ServerFileView.class);
        i.putExtra("userPublicKey", publicKeyAsString);
        i.putExtra("userWantsTo", "Upload");
        startActivity(i);
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

        Log.i("initializeKeys", "Do keys exist = " + (keyGenerator.getPublicKeyAsString() == null));
        Log.i("initializeKeys", "Key is " + keyGenerator.getPublicKeyAsString());
        if (keyGenerator.getPublicKeyAsString().equals("") || keyGenerator.getPublicKeyAsString() == null) {
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

        Log.i("pk ", "Pk = {" + publicKeyAsString + "}");
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
                databaseController = new DatabaseController(this);
                Log.i("onResumeMA", "databaseControllerObject created");
                if (!databaseController.checkIfValidUser(deviceIdString, publicKeyAsString)) {
                    Log.i("onResume", "Device ID:{" + deviceIdString + "}. Pub Key: {" + publicKeyAsString + "}");
                    finish();
                }
            } catch (IllegalStateException e) {
                Log.e("onResumeMA", e.toString(), e);
            }
        }
    }

    /**
     * getPermissions
     * <p/>
     * checks if an app has the needed permissions and updates 'hasPermissions' accordingly. If this is run
     * on a pre API 23 device it will just set 'hasPermissions' to true. If run on an API 23 device onwards, this
     * method will open the runtime permission request windows if permissions are missing. If permissions are
     * permanently denied displays a warning.
     */
    private void getPermissions() {
        boolean needToShowRationale = false; //is there any permission that has been permanently denied / first time
        ArrayList<String> neededPerms = new ArrayList<String>(); //list of not-granted permissions
        for (String currentPermission : neededPermissions) {//go through all possible permissions
            //get current permission state for this permission
            int hasThisPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                    currentPermission);
            if (hasThisPermission != PackageManager.PERMISSION_GRANTED) {
                neededPerms.add(currentPermission);
                //is this permission permanently denied / first time ask
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        currentPermission)) {
                    needToShowRationale = true;
                }
            }
        }
        //if list is empty, no needed permissions, ie all permissions already granted
        if (neededPerms.size() <= 0) {
            hasPermissions = true;
            return;
        }
        final String[] permissionList = neededPerms.toArray(new String[1]);
        if (needToShowRationale) {
            //make message box explaining reason these permissions are needed, opens permission box if 'ok' pressed
            showMessageOKCancel("Please allow access to all these functions",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    permissionList,
                                    REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    }
            );
        } else { //not all permissions granted, but none permanently denied
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionList,
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    /**
     * onRequestPermissionResult
     * <p/>
     * The callback return method when a "Request Permission" dialog window closes.
     *
     * @param requestCode  : the code passed on the dialog window's creation
     * @param permissions  : the list of permissions passed to the dialog window on it's creation
     * @param grantResults : the resulting state of the permissions in the previous parameter
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                //go through all permission statuses, if any still denied mark hasPermissions = false
                for (Integer result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        hasPermissions = false;
                        return;
                    }
                }
                hasPermissions = true;
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    //Used to create a dialog window to display a warning when permissions are permanently denied.
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}