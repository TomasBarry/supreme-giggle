package tomas.giggle;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class ConfirmActivity extends Activity {

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_confirm);

        Log.i("onCreate_CA", "Beginning onCreate for ConfirmActivity");

        Bundle extras = getIntent().getExtras();
        String uriPath = extras.getString("uriPath");
        String action = extras.getString("userWantsTo");
        assert action != null;

        this.filePath = getRealPathFromURI(Uri.parse(uriPath));
        String fileName = new File(filePath).getName();

        TextView prompt = (TextView) findViewById(R.id.confirmation_prompt);
        prompt.setText(getResources().getString(
                R.string.header_confirm_prompt, action.toLowerCase(), fileName));
    }

    public void confirm(View v) throws InterruptedException {
        Log.i("confirm", "User has confirmed");
        new DropboxCommunicator(this).uploadFile(filePath);
        finish();
    }

    public void cancel(View v) {
        Log.i("cancel", "User has canceled");
        finish();
    }

    public String getRealPathFromURI(Uri uri) {
        Log.i("getRealPathFromURI", "About to get real path");
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String retString =
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        cursor.close();
        Log.i("getRealPathFromURI", "Real path is " + retString);
        return retString;
    }
}
