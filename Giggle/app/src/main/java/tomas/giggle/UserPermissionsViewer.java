package tomas.giggle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

public class UserPermissionsViewer extends Activity {

    private ListView list;
    private TextView fileNameTextView;
    private String fileNameString;
    private Hashtable<String, Boolean> userNamesAndPerms;
    private String userPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_view);
        Log.i("onCreate_UPV", "Starting on create");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.userPublicKey = extras.getString("userPublicKey");
            this.fileNameString = extras.getString("fileName");
        }

        this.fileNameTextView = (TextView) findViewById(R.id.file_or_dir_name);
        this.fileNameTextView.setText(this.fileNameString);

        this.userNamesAndPerms = MainActivity.databaseController.getUsersAndPermissionsFor(userPublicKey, fileNameString);

        ArrayList<String> keys = new ArrayList<String>(this.userNamesAndPerms.keySet());
        ArrayList<Boolean> values = new ArrayList<Boolean>(this.userNamesAndPerms.values());

        this.list = (ListView) findViewById(R.id.permissioned_users);

        list.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        R.layout.list_view_row_just_text,
                        R.id.file_or_dir_name,
                        keys)
        );

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                        Log.i("onCreate_UPV", "User wants to see permissions for ");
                    }
                }
        );

        Log.i("onCreate_UPV", "Ending on create");
    }
}
