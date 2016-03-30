package tomas.giggle;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Tomas on 30/03/2016.
 */
public class ConfirmActivity extends Activity {

    private TextView prompt;

    private Button noButton;
    private Button yesButton;

    private String action;
    private String filePath;
    private String fileName;
    private String uriPath;

    private DropboxCommunicator dc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_confirm);

        Log.i("onCreate_CA", "Beginning onCreate for ConfirmActivity");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.uriPath = extras.getString("uriPath");
            this.action = extras.getString("userWantsTo");

            this.dc = new DropboxCommunicator(this);
            this.filePath = dc.getRealPathFromURI(Uri.parse(uriPath));

            this.fileName = new File(filePath).getName();

            Log.i("onCreate_CA", "File is " + this.fileName + " and user wants to " + this.action);
        }
        Resources res = getResources();
        this.prompt = (TextView) findViewById(R.id.confirmation_prompt);
        this.prompt.setText(res.getString(R.string.header_confirm_prompt, this.action.toLowerCase(), this.fileName));

        this.noButton = (Button) findViewById(R.id.no);
        this.yesButton = (Button) findViewById(R.id.yes);


    }

    public void confirm(View v) {
        Log.i("confirm", "User has confirmed");
        dc.uploadFile(filePath);
        finish();
    }

    public void cancel(View v) {
        Log.i("cancel", "User has canceled");
        finish();
    }
}
