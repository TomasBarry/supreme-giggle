//package tomas.giggle;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//
//import com.dropbox.client2.DropboxAPI;
//import com.dropbox.client2.android.AndroidAuthSession;
//import com.dropbox.client2.session.AppKeyPair;
//
//
//public class ConnectToDropBoxActivity extends AppCompatActivity {
//
//    // Used to connect to DropBox App
//    private final String APP_KEY = SecretConstants.APP_KEY;
//    private final String APP_SECRET = SecretConstants.APP_SECRET;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i("ConnectToDropBoxAct", "Connecting to DropBox");
//
//        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
//        AndroidAuthSession session = new AndroidAuthSession(appKeys);
//        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
//        Log.i("ConnectToDropBoxAct", "mDBApi created");
//        mDBApi.getSession().startOAuth2Authentication(ConnectToDropBoxActivity.this);
//        Log.i("ConnectToDropBoxAct", "mDBApi getSession started");
//
//        Log.i("ConnectToDropBoxAct", "Connected to DropBox");
//    }
//
//
//    protected void onResume() {
//        super.onResume();
//        if (mDBApi.getSession().authenticationSuccessful()) {
//            try {
//                // Required to complete auth, sets the access token on the session
//                // The finishAuthentication() method will bind the user's access token to the
//                // session. You'll now be able to retrieve it via
//                // mDBApi.getSession().getOAuth2AccessToken().
//                MainActivity.mDBApi.getSession().finishAuthentication();
//                // String accessToken = MainActivity.mDBApi.getSession().getOAuth2AccessToken();
//                Log.i("onResume", "resuming activity");
//                finish();
//            } catch (IllegalStateException e) {
//                Log.e("onResume", e.toString(), e);
//            }
//        }
//    }
//}
