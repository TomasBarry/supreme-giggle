package tomas.giggle;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


public class DownloadFileActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_viewer);
        Log.i("onCreate_DFA", "Starting on create");

        try {
            Bundle extras = getIntent().getExtras();
            String fileName = extras.getString("fileName");
            DropboxCommunicator dc = new DropboxCommunicator(this);

            TextView header = (TextView) findViewById(R.id.downloaded_file_name);
            header.setText(getResources().getString(R.string.header_file_name, fileName));

            ImageView image = (ImageView) findViewById(R.id.downloaded_image);
            image.setImageBitmap(
                    dc.downloadFile(fileName, new KeyGenerator(this).getPublicKeyAsString()));
        } catch (Exception e) {
            Log.e("onCreate_DFA", e.toString(), e);
        }
        Log.i("onCreate_DFA", "Finished onCreate");
    }
}
