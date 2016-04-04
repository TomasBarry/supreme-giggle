package tomas.giggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class PersonalFileView extends Activity {


    private String[] fileNamesForUser;
    private DatabaseController dbc = MainActivity.databaseController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_file_view);
        Log.i("onCreate_PFV", "Starting on create");

        this.fileNamesForUser = dbc.getFilesFor(new KeyGenerator(this).getPublicKeyAsString());

        ListView list = (ListView) findViewById(R.id.owned_files_list);
        list.setAdapter(
                new ArrayAdapter<>(
                        this,
                        R.layout.list_view_row_just_text,
                        R.id.file_or_dir_name,
                        fileNamesForUser)
        );
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        Log.i("onCreate_PFA", "User wants to see permissions for "
                                + fileNamesForUser[myItemInt]);
                        goToPermsView(fileNamesForUser[myItemInt]);
                    }
                }
        );
        Log.i("onCreate_PFV", "Ending on create");
    }


    public void goToPermsView(String fileName) {
        Intent i = new Intent(this, UserPermissionsViewer.class);
        i.putExtra("fileName", fileName);
        startActivity(i);
    }
}
