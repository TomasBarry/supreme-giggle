package tomas.giggle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

        ArrayList<UserPermission> usersAndPerms = new ArrayList<>();

        ArrayList<String> keys = new ArrayList<String>(this.userNamesAndPerms.keySet());

        for (String key : keys) {
            Log.i("onCreate_UPV", "User: " + key + "| Switch: " + this.userNamesAndPerms.get(key));
            usersAndPerms.add(new UserPermission(key, this.userNamesAndPerms.get(key)));
        }

        this.list = (ListView) findViewById(R.id.permissioned_users);

        this.list.setAdapter(new MyCustomBaseAdapter(this, usersAndPerms));

//        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//                Log.i("onCreate_UPV", "gets here");
//                Object o = list.getItemAtPosition(position);
//                UserPermission fullObject = (UserPermission) o;
//                Log.i("onCreate_UPV", "" + fullObject.getUserName());
//            }
//        });
//
//
//        list.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
//                        Log.i("onCreate_UPV", "User wants to see permissions for ");
//                    }
//                }
//        );

        Log.i("onCreate_UPV", "Ending on create");
    }
}
