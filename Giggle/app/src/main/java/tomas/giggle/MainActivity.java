package tomas.giggle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    private static final String APP_KEY = SecretConstants.APP_KEY;
    private static final String APP_SECRET = SecretConstants.APP_SECRET;

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) findViewById(R.id.createFileButton);

        Log.d("Giggle_onCreate", "Button b created");

        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        Log.d("Giggle_onCreate", "mDBApi created");


        // MainActivity below should be your activity class name
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);

        Log.d("Giggle_onCreate", "mDBApi getSession started");

        b.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                addFile();
            }
        });

        Log.d("Giggle_onCreate", "b click listener started");
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
