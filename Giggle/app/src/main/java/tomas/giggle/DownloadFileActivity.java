package tomas.giggle;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadFileActivity extends Activity {

    private TextView header;
    private ImageView image;

    private String fileName;
    private String userPublicKey;

    private DropboxCommunicator dc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_viewer);
        Log.i("onCreate_DFA", "Starting on create");

        this.header = (TextView) findViewById(R.id.downloaded_file_name);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.userPublicKey = extras.getString("userPublicKey");
            this.fileName = extras.getString("fileName");

            Resources res = getResources();
            this.header.setText(res.getString(R.string.header_file_name, this.fileName));
        }

        this.image = (ImageView) findViewById(R.id.downloaded_image);
        this.dc = new DropboxCommunicator(this);
        Bitmap bitmap = dc.downloadFile(this.fileName, this.userPublicKey);
//        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/Screenshots/Screenshot_2016-03-29-00-20-18.png");
        this.image.setImageBitmap(bitmap);
        Log.i("onCreate_DFA", "Finished onCreate");
    }

}
