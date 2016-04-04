package tomas.giggle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class DropboxCommunicator {


    private Context context;
    private String encKey;
    private Bitmap bitmap = null;
    private File encryptedFile;

    private boolean decrypted = false;
    private boolean finished = false;


    /**
     * Constructor
     *
     * @param context: the context
     */
    public DropboxCommunicator(Context context) {
        this.context = context;
    }


    /**
     * convert a file to an array of bytes
     *
     * @param f: the file
     * @return the file as an array of bytes
     */
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


    /**
     * convert an array of bytes to a file
     *
     * @param bytes:    the array of bytes
     * @param fileName: the name of the file to create
     * @return the file
     */
    public File bytesToFile(byte[] bytes, String fileName) {
        Log.i("bytesToFile", "About to put bytes into file called " + fileName);
        try {
            File f = File.createTempFile(fileName, "");
            f.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            fos.close();
            return f;
        } catch (Exception e) {
            Log.e("bytesToFile", e.toString(), e);
        }
        Log.i("bytesToFile", "Attempted to write bytes but failed");
        return null;
    }


    /**
     * encrypt a file using AES
     *
     * @param fileName:   the name of the file
     * @param fileBytes:  the file as an array of bytes
     * @param keyHandler: a key handler object to encrypt the file
     * @return the encrypted File object
     */
    public File encryptFile(String fileName, byte[] fileBytes, SymmetricKeyHandler keyHandler) {
        Log.i("encryptFile", "About to encrypt the file " + fileName);
        try {
            String key = Base64.encodeToString(keyHandler.getBinaryDataKey(), Base64.DEFAULT);

            EncryptionKey encryptionKey =
                    new EncryptionKey(key, new KeyGenerator(context).getPublicKeyAsString(), false, context);

            this.encKey = encryptionKey.getEncryptedKey();

            SecretKey secretKeySpec =
                    new SecretKeySpec(Base64.decode(key, Base64.DEFAULT), "AES");
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


    /**
     * decrypt a file using AES
     *
     * @param f:         the file to decrypt
     * @param publicKey: the users public key
     * @param fileName:  the name of the file to create
     * @return the decrypted file
     */
    public File decryptFile(File f, String publicKey, String fileName) {
        Log.i("decryptFile", "About to decrypt the file " + f.getName());
        try {
            byte[] encryptedBytes = fileToBytes(f);

            String key = MainActivity.databaseController.getEncKeyFor(fileName);

            EncryptionKey encryptionKey =
                    new EncryptionKey(key, new KeyGenerator(context).getPublicKeyAsString(), true, context);

            String decryptedKey = encryptionKey.getDecryptedKey();

            SecretKey secretKeySpec =
                    new SecretKeySpec(Base64.decode(decryptedKey, Base64.DEFAULT), "AES");
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


    /**
     * upload a file to Dropbox
     *
     * @param path: the path to the file to upload
     * @throws InterruptedException
     */
    public void uploadFile(String path) throws InterruptedException {
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
        MainActivity.databaseController.addFileEntryIntoFileKeys(
                this.encKey, new KeyGenerator(context).getPublicKeyAsString(), f.getName(), 1);
        Log.i("uploadFile", "Finished uploading file");
    }


    /**
     * wait for an event to occur
     *
     * @throws InterruptedException
     */
    private void customWait() throws InterruptedException {
        while (!finished) {
            Thread.sleep(1);
        }
        finished = false;
    }


    /**
     * download a file from Dropbox
     *
     * @param fName: the name of the file
     * @param pKey:  the public ke of the user
     * @return a Bitmap for the downloaded image
     * @throws InterruptedException
     */
    public Bitmap downloadFile(String fName, String pKey) throws InterruptedException {
        Log.i("downloadFile", "About to download file " + fName);
        final String fileName = fName;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    encryptedFile = File.createTempFile(fileName, "");
                    encryptedFile.deleteOnExit();
                    FileOutputStream outputStream = new FileOutputStream(encryptedFile);
                    DropboxAPI.DropboxFileInfo info =
                            MainActivity.mDBApi.getFile(fileName, null, outputStream, null);
                    Log.i("downloadFile", "The file's rev is: " + info.getMetadata().rev);
                    finished = true;
                } catch (Exception e) {
                    Log.e("downloadFile", e.toString(), e);
                }
            }
        });
        customWait();
        File f = decryptFile(encryptedFile,
                new KeyGenerator(context).getPublicKeyAsString(), fileName);
        bitmap = BitmapFactory.decodeFile(f.getPath());
        Log.i("downloadFile", "Downloaded  the file and returning the bitmap");
        return bitmap;
    }
}