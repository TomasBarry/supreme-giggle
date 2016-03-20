package tomas.giggle;

import android.content.Context;
import android.content.res.Resources;
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
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String APP_KEY = SecretConstants.APP_KEY;
    private static final String APP_SECRET = SecretConstants.APP_SECRET;

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button encryptButton = (Button) findViewById(R.id.encrypt_button);
        Button decryptButton = (Button) findViewById(R.id.decrpyt_button);

        assert encryptButton !=  null;
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
        Log.d("Giggle_onCreate", "Buttons and listeners created");

        TextView uniqueDeviceId = (TextView) findViewById(R.id.unique_device_id);
        TextView publicKeyText = (TextView) findViewById(R.id.public_key_value);
        TextView privateKeyText = (TextView) findViewById(R.id.private_key_value);

        assert uniqueDeviceId != null;
        Resources res = getResources();
        String text = res.getString(R.string.device_id_string, getUniqueAndroidDeviceId());
        uniqueDeviceId.setText(text);
        Log.d("Giggle_onCreate", "Text views created");


        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        Log.d("Giggle_onCreate", "mDBApi created");


        // MainActivity below should be your activity class name
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
        Log.d("Giggle_onCreate", "mDBApi getSession started");
    }

    public String getUniqueAndroidDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("Giggle_getUniqueAndroid", "tm created");
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Log.d("Giggle_getUniqueAndroid", "strings created");

        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        Log.d("Giggle_getUniqueAndroid", "UUID created");
        return deviceUuid.toString();
    }

    public void addFile() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = File.createTempFile("working-draft", "txt");
                    file.deleteOnExit();
                    FileInputStream inputStream = new FileInputStream(file);
                    DropboxAPI.Entry response = mDBApi.putFile("/magnum-opus.txt", inputStream,
                            file.length(), null, null);
                    Log.d("Giggle_addFile", "The uploaded file's rev is: " + response.rev);
                } catch (Exception e) {
                    Log.e("Giggle_addFile", "IOException", e);
                }
            }
        });
    }


    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                // The finishAuthentication() method will bind the user's access token to the
                // session. You'll now be able to retrieve it via
                // mDBApi.getSession().getOAuth2AccessToken().
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.d("Giggle_onResume", "Error authenticating", e);
            }
        }
    }

}
