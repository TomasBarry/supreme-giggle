package tomas.giggle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Tomas on 21/03/2016.
 */
public class FileBrowserActivity extends AppCompatActivity {

    private ListView list;
    private String[] fileNames = null;
    ArrayList<DropboxAPI.Entry> dropboxFiles;
    AlertDialog.Builder builder;
    Button uploadButton;
    String selectedFileString;
    File selectedFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        setUpFilesView();
        initList();
        initBuilder();
        initButton();

    }

    public void initBuilder() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Edit button
                        Log.i("initBuilder", "User wants to edit file");
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("initBuilder", "Downloading file: " + selectedFileString);
                                selectedFile = downloadFile(selectedFileString);
                            }
                        });
                        try {
                            while (selectedFile == null) {
                                Log.i("initBuilder", "Waiting");
                                Thread.sleep(250);
                            }
                            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                            String line = null;
                            while ((line = br.readLine()) != null)
                            {
                                Log.i("initBuilder", line);
                            }
                        }
                        catch (Exception e) {
                            Log.e("initBuilder", "Exception: " + e.toString(), e);
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Delete Button
                        Log.i("initBuilder", "User wants to delete file");
                        break;
                }
            }
        };

        builder = new AlertDialog.Builder(FileBrowserActivity.this);
        builder.setMessage("What do you want to do?").setPositiveButton("Edit", dialogClickListener)
                .setNegativeButton("Delete", dialogClickListener);
    }

    public File downloadFile(String fileName) {
        File file = null;
        try {
            file = File.createTempFile(fileName, ".txt");
            file.deleteOnExit();
            FileOutputStream outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info =
                    MainActivity.mDBApi.getFile(fileName, null, outputStream, null);
            Log.i("downloadFile", "The file's rev is: " + info.getMetadata().rev);
        }
        catch (Exception e) {
            Log.e("downloadFile", "Exception: " + e.toString(), e);
        }
        return file;
    }

    public String[] getFilesArray() {
        String[] fnames = null;
        try {
            DropboxAPI.Entry dirent = MainActivity.mDBApi.metadata("/", 1000, null, true, null);
            Log.i("getFilesArray", "Get entry metatdata from DropBox");
            dropboxFiles = new ArrayList<DropboxAPI.Entry>();
            ArrayList<String> dir = new ArrayList<String>();
            int i = 0;
            for (DropboxAPI.Entry ent : dirent.contents) {
                dropboxFiles.add(ent);// Add it to the list of thumbs we can choose from
                //dir = new ArrayList<String>();
                dir.add(new String(dropboxFiles.get(i++).path));
            }
            Log.i("getFilesArray", "Iterated over files");
            fnames = dir.toArray(new String[dir.size()]);
        } catch (Exception e) {
            Log.e("getFilesArray", "Exception: " + e.toString(), e);
        }
        return fnames;
    }

    public void initButton() {
        uploadButton = (Button) findViewById(R.id.upload_button);
        assert uploadButton != null;
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Log.i("initButton", "User wants to upload a file");
            }
        });
    }

    public void setUpFilesView() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                fileNames = getFilesArray();
            }
        });
        try {
            while (fileNames == null) {
                Log.i("setUpFilesView", "Waiting");
                Thread.sleep(250);
            }
        }
        catch (Exception e) {
            Log.e("setUpFilesView", "Exception: " + e.toString(), e);
        }
    }

    public void initList() {
        list = (ListView)findViewById(R.id.fileBrowserList);
        list.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        R.layout.file_browser_row, //use this layout as a ListView row
                        R.id.filenametext, //place the String data into this Textview
                        fileNames) //get info from this array
        );

        //set up ListView OnClick Listener, this is for the row, not the button on the list row
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        Log.i("initList", "File clicked: " + fileNames[myItemInt]);
                        selectedFileString = fileNames[myItemInt];
                        builder.show();
                    }
                }
        );
    }
}
