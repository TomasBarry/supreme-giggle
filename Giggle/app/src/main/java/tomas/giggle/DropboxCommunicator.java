package tomas.giggle;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DropboxCommunicator {

    private Context context;
    private String encKey;
    private Bitmap bitmap = null;

    public DropboxCommunicator(Context context) {
        this.context = context;
    }

    public byte[] fileToBytes(File f) {
        try {
            byte[] plainFileBytes = new byte[(int) f.length()];
            new FileInputStream(f).read(plainFileBytes);
            return plainFileBytes;
        } catch (Exception e) {
            Log.e("fileToBytes", e.toString(), e);
        }
        return null;
    }

    public File bytesToFile(byte[] bytes, String fileName) {
        Log.i("bytesToFile", "About to put bytes into file called " + fileName);
        try {
            File f = File.createTempFile(fileName, "");
            f.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            fos.close();
            Log.i("bytesToFile", "Written bytes to file");
            return f;
        } catch (Exception e) {
            Log.e("bytesToFile", e.toString(), e);
        }
        Log.i("bytesToFile", "Attempted to write bytes but failed");
        return null;
    }

    public File encryptFile(String fileName, byte[] fileBytes, SymmetricKeyHandler keyHandler) {
        Log.i("encryptFile", "About to encrypt the file " + fileName);
        try {
            String key = new Base64Translator(context).fromBinary(keyHandler.getBinaryDataKey());

            EncryptionKey encryptionKey =
                    new EncryptionKey(key, new KeyGenerator(context).getPublicKeyAsString(), false, context);
            Log.i("encryptFile", "encrypted " + encryptionKey.getEncryptedKey());
            Log.i("encryptFile", "plain " + encryptionKey.getPlainKey());
            Log.i("encryptFile", "public " + encryptionKey.getPublicKey());

            this.encKey = encryptionKey.getEncryptedKey();

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(new Base64Translator(context).toBinary(encryptionKey.getEncryptedKey()), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encodedBytes = c.doFinal(fileBytes);

            File f = bytesToFile(encodedBytes, "ENCODED_" + fileName);
            Log.i("encryptFile", "Encrypted file " + fileName + " as " + f.getName());
            return f;
        } catch (Exception e) {
            Log.e("encryptFile", e.toString(), e);
        }
        Log.i("encryptFile", "Attempted to encrypt file but failed");
        return null;
    }

    public File decryptFile(File f, String publicKey) {
        Log.i("decryptFile", "About to decrypt the file " + f.getName());
        try {
            byte[] encryptedBytes = fileToBytes(f);

            String key = MainActivity.databaseController.getEncKeyFor(f.getName());
            EncryptionKey encryptionKey =
                    new EncryptionKey(key, new KeyGenerator(context).getPublicKeyAsString(), true, context);
            Log.i("encryptFile", "encrypted " + encryptionKey.getEncryptedKey());
            Log.i("encryptFile", "decrypted " + encryptionKey.getDecryptedKey());
            Log.i("encryptFile", "private " + encryptionKey.getPrivateKey());

            String decryptedKey = encryptionKey.getDecryptedKey();


            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(new Base64Translator(context).toBinary(encryptionKey.getDecryptedKey()), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodedBytes = c.doFinal(encryptedBytes);
            Log.i("decryptFile", "Decrypted the file woo hoo :D");
            return bytesToFile(decodedBytes, f.getName().substring(8, f.getName().length() - 1));

        } catch (Exception e) {
            Log.e("decryptFile", e.toString(), e);
        }
        Log.i("decryptFile", "Couldn't decrypt file, returning null");
        return null;
    }

    public void uploadFile(String path) {
        Log.i("uploadFile", "About to upload file at " + path);
        File plainFile = new File(path);

        SymmetricKeyHandler symmetricKeyHandler = new SymmetricKeyHandler(context);
        byte[] plainFileBytes = fileToBytes(plainFile);

        final File f = encryptFile(plainFile.getName(), plainFileBytes, symmetricKeyHandler);

        Log.i("uploadFile", "Uploading file at " + path);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream inputStream = new FileInputStream(f);
                    DropboxAPI.Entry response = MainActivity.mDBApi.putFile(f.getName(), inputStream,
                            f.length(), null, null);
                    Log.i("uploadFile", "The uploaded file's rev is: " + response.rev);
                } catch (Exception e) {
                    Log.e("uploadFile", e.toString(), e);
                }
                Log.i("AsyncTask", "File added :)");
            }
        });
        // Add keys to table :D
        MainActivity.databaseController.addFileEntryIntoFileKeys(this.encKey, new KeyGenerator(context).getPublicKeyAsString(), f.getName(), 1);
        Log.i("uploadFile", "Finished uploading file");
    }

    public Bitmap downloadFile(String fName, String pKey) {
        Log.i("downloadFile", "About to download file " + fName);
        final String fileName = fName;
        final String publicKey = pKey;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File encryptedFile = File.createTempFile(fileName, "");
                    encryptedFile.deleteOnExit();
                    FileOutputStream outputStream = new FileOutputStream(encryptedFile);
                    DropboxAPI.DropboxFileInfo info =
                            MainActivity.mDBApi.getFile(fileName, null, outputStream, null);
                    Log.i("downloadFile", "The file's rev is: " + info.getMetadata().rev);
                    File f = decryptFile(encryptedFile, publicKey);
                    bitmap = BitmapFactory.decodeFile(f.getPath());

                } catch (Exception e) {
                    Log.e("downloadFile", e.toString(), e);
                }
            }
        });
        waitForBitmap();
        Log.i("downloadFile", "Downloaded  the file and returning the bitmap");
        return bitmap;
    }

    private void waitForBitmap() {
        Log.i("waitForBitmap", "Waiting to get file back from DropBox");
        try {
            while (this.bitmap == null) {
                Thread.sleep(250);
                Log.i("waitForBitmap", "Waiting");
            }
        } catch (Exception e) {
            Log.e("waitForBitmap", e.toString(), e);
        }
        Log.i("waitForBitmap", "Got file back from DropBox");
    }

    public String getRealPathFromURI(Uri uri) {
        Log.i("getRealPathFromURI", "About to get real path");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        Log.i("getRealPathFromURI", "Real path is " + cursor.getString(idx));
        return cursor.getString(idx);
    }
}
