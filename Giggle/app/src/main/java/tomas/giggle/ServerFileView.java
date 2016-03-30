package tomas.giggle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ServerFileView extends Activity {

    private ListView list;
    private TextView headerString;
    private String[] fileNamesOnServer;
    private String userPublicKey;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_file_view);
        Log.i("onCreate_SFV", "Starting on create");

        this.headerString = (TextView) findViewById(R.id.server_files_header);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.userPublicKey = extras.getString("userPublicKey");
            this.action = extras.getString("userWantsTo");
        }

        switch (action) {
            case "Upload":
                uploadHandler();
                break;
            case "Download":
                downloadHandler();
                break;
            default:
                Log.i("onCreate_SFV", "Shouldn't get here");
        }
        Log.i("onCreate_SFV", "Ending on create");
    }

    public void downloadHandler() {
        Log.i("downloadHandler", "Beginnig download hander");

        this.fileNamesOnServer = MainActivity.databaseController.getFilesOnServer();

        this.list = (ListView) findViewById(R.id.server_files_list);

        list.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        R.layout.list_view_row_just_text,
                        R.id.file_or_dir_name,
                        fileNamesOnServer)
        );

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        Log.i("downloadHandler", "User wants to download " + fileNamesOnServer[myItemInt]);
                        goToActionView(fileNamesOnServer[myItemInt]);
                    }
                }
        );
        Log.i("downloadHandler", "Ending download handler");
    }

    public void uploadHandler() {
        Log.i("uploadHandler", "Beginnig upload handler");
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
        Log.i("uploadHandler", "Ending upload handler");
    }

    public void goToActionView(String fileName) {
        Log.i("goToActionView", "User wants to " + this.action + " a file called " + fileName);
//        Intent i = new Intent(this, UserPermissionsViewer.class);
//        i.putExtra("userPublicKey", this.userPublicKey);
//        i.putExtra("fileName", fileName);
//        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult_SFV", "Came back to ServerFileView");
        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            String path = targetUri.toString();
            Log.i("onActivityResult_SFV", "Path is " + path);
            Intent i = new Intent(this, ConfirmActivity.class);
            i.putExtra("filePath", path);
            i.putExtra("userWantsTo", this.action);
            startActivity(i);
            finish();
        }
    }
}
