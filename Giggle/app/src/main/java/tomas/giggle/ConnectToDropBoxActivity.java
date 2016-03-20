package tomas.giggle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by Tomas on 20/03/2016.
 */
public class ConnectToDropBoxActivity extends AppCompatActivity {

    // Used to connect to DropBox App
    private static final String APP_KEY = SecretConstants.APP_KEY;
    private static final String APP_SECRET = SecretConstants.APP_SECRET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToDropBox();
    }

    /**
     * connectToDropBox
     * <p/>
     * Set up connection to DropBox and ask for authorization
     */
    public void connectToDropBox() {
        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        MainActivity.mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        Log.d("connectToDropBox", "mDBApi created");


        // MainActivity below should be your activity class name
        MainActivity.mDBApi.getSession().startOAuth2Authentication(ConnectToDropBoxActivity.this);
        Log.d("connectToDropBox", "mDBApi getSession started");
    }

    protected void onResume() {
        super.onResume();
        if (MainActivity.mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                // The finishAuthentication() method will bind the user's access token to the
                // session. You'll now be able to retrieve it via
                // mDBApi.getSession().getOAuth2AccessToken().
                MainActivity.mDBApi.getSession().finishAuthentication();
                String accessToken = MainActivity.mDBApi.getSession().getOAuth2AccessToken();
                Log.d("onResume", "resuming activity");
                Log.d("onResume", MainActivity.mDBApi.getSession().getOAuth2AccessToken() + "");
                finish();
            } catch (IllegalStateException e) {
                Log.e("onResume", "Error authenticating", e);
            }
        }
    }
}
