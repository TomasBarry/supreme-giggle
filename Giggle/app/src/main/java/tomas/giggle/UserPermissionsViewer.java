package tomas.giggle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;


public class UserPermissionsViewer extends Activity {


    private String fileNameString;
    private DatabaseController dbc = MainActivity.databaseController;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_view);
        Log.i("onCreate_UPV", "Starting on create");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.fileNameString = extras.getString("fileName");
        }

        TextView fileNameTextView = (TextView) findViewById(R.id.file_or_dir_name);
        fileNameTextView.setText(this.fileNameString);

        Hashtable<String, Boolean> userNamesAndPerms = dbc.getUsersAndPermissionsFor(
                new KeyGenerator(this).getPublicKeyAsString(), fileNameString);

        ArrayList<UserPermission> usersAndPerms = new ArrayList<>();

        ArrayList<String> keys = new ArrayList<>(userNamesAndPerms.keySet());

        for (String key : keys) {
            Log.i("onCreate_UPV", "User: " + key + "| Switch: " + userNamesAndPerms.get(key));
            usersAndPerms.add(new UserPermission(key, userNamesAndPerms.get(key)));
        }
        ListView list = (ListView) findViewById(R.id.permissioned_users);
        list.setAdapter(new MyCustomBaseAdapter(this, usersAndPerms, fileNameString));
        Log.i("onCreate_UPV", "Ending on create");
    }
}
