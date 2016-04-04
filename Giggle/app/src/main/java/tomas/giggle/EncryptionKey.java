package tomas.giggle;


import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;


public class EncryptionKey {


    private String plainKey;
    private String encryptedKey;
    private String publicKey;
    private String privateKey;
    private String decryptedKey;
    private Context context;

    /**
     * constructor
     *
     * @param key:         the key
     * @param userKey:     the users public key
     * @param isEncrypted: whether the key is encrypted or not
     * @param context:     the context
     */
    public EncryptionKey(String key, String userKey, boolean isEncrypted, Context context) {
        Log.i("EncryptionKey", "Beginning constructor where isEncrypted is " + isEncrypted);

        this.context = context;

        // want the key decrypted
        if (isEncrypted) {
            this.decryptKey(key, userKey);
        }
        // want the key encrypted
        else {
            this.encryptKey(key, userKey);
        }
        Log.i("EncryptionKey", "Finsihed constructor");
    }


    /**
     * encrypt a plain key
     *
     * @param plainKey:  the plain key to encrypt
     * @param publicKey: the public key of the user
     */
    private void encryptKey(String plainKey, String publicKey) {
        Log.i("encryptKey", "About to encrypt " + plainKey + " with " + publicKey);
        this.plainKey = plainKey;
        this.publicKey = publicKey;
        PublicKey key0 = new KeyGenerator(context).getPublicKey();
        PublicKey key1 = new KeyGenerator(context).getPublicKey();
        Log.i("encryptKey", "Are the keys equal: " + key0.equals(key1));

        PublicKey key = new KeyGenerator(context).generateKeyFromString(publicKey);

        byte[] transKeyBinary = Base64.decode(plainKey, Base64.DEFAULT);
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedBytes = cipher.doFinal(transKeyBinary);
            this.encryptedKey = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            Log.i("encryptKey", "The encrypted key is " + encryptedKey);
        } catch (Exception e) {
            Log.e("encryptKey", e.toString(), e);
        }
        Log.i("encryptKey", "Finished encrypting key");
    }


    /**
     * decrypt the encrypted key
     *
     * @param encryptedKey: the encrypted key
     * @param privateKey:   the private key of the user
     */
    private void decryptKey(String encryptedKey, String privateKey) {
        Log.i("decryptKey", "About to decrypt " + encryptedKey + " with " + privateKey);
        this.encryptedKey = encryptedKey;
        this.privateKey = privateKey;

        PrivateKey key = new KeyGenerator(context).getPrivateKey();
        PrivateKey key1 = new KeyGenerator(context).getPrivateKey();
        Log.i("encryptKey", "Are the keys equal: " + key.equals(key1));
        byte[] transKeyBinary = Base64.decode(encryptedKey, Base64.DEFAULT);
        byte[] decryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedBytes = cipher.doFinal(transKeyBinary);
            this.decryptedKey = Base64.encodeToString(decryptedBytes, Base64.DEFAULT);
            Log.i("decryptKey", "Decrypted Key: " + decryptedKey);
        } catch (Exception e) {
            Log.e("decryptKey", e.toString(), e);
        }
        Log.i("decryptKey", "Finished decrypting key");
    }


    // GETTERS


    public String getPlainKey() {
        return plainKey;
    }


    public String getEncryptedKey() {
        return encryptedKey;
    }


    public String getDecryptedKey() {
        return decryptedKey;
    }

    public String getPublicKey() {
        return publicKey;
    }


    public String getPrivateKey() {
        return privateKey;
    }
}
