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
        this.list.setAdapter(new MyCustomBaseAdapter(this, usersAndPerms, fileNameString));

        Log.i("onCreate_UPV", "Ending on create");

        SymmetricKeyHandler h = new SymmetricKeyHandler(this);
        SymmetricKeyHandler j = new SymmetricKeyHandler(this,
                new Base64Translator(this).toBinary(MainActivity.databaseController.getEncKeyFor(fileNameString)));

//        EncryptionKey encryptionKeyBefore =
//                new EncryptionKey(
//                        MainActivity.databaseController.getEncKeyFor(fileNameString, userPublicKey),
//                        userPublicKey, false, this);
//        Log.i("addPermissionFor", "encryptionKey.plainKey " + encryptionKeyBefore.getPlainKey());
//        Log.i("addPermissionFor", "encryptionKey.encryptedKey " + encryptionKeyBefore.getEncryptedKey());
//        Log.i("addPermissionFor", "encryptionKey.publicKey " + encryptionKeyBefore.getPublicKey());
//
//        EncryptionKey encryptionKeyAfter =
//                new EncryptionKey(
//                        encryptionKeyBefore.getEncryptedKey(),
//                        new tomas.giggle.KeyGenerator(this).getPrivateKeyAsString(), true, this);
//        Log.i("addPermissionFor", "encryptionKey.decryptedKey " + encryptionKeyAfter.getDecryptedKey());
//        Log.i("addPermissionFor", "encryptionKey.encryptedKey " + encryptionKeyAfter.getEncryptedKey());
//        Log.i("addPermissionFor", "encryptionKey.privateKey " + encryptionKeyAfter.getPrivateKey());
//
//        Log.i("PZL", "Are dey eqwal decrypted?? " + encryptionKeyBefore.getPlainKey().equals(encryptionKeyAfter.getDecryptedKey()));
//        Log.i("PZL", "Are dey eqwal encryted??? " + encryptionKeyBefore.getEncryptedKey().equals(encryptionKeyAfter.getEncryptedKey()));
    }
}
