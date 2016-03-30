package tomas.giggle;


import android.content.Context;
import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * Created by Tomas on 29/03/2016.
 */
public class EncryptionKey {

    private String plainKey;
    private String encryptedKey;
    private String publicKey;
    private String privateKey;
    private String decryptedKey;

    private boolean isEncrypted;

    private Context context;

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

    private void encryptKey(String plainKey, String publicKey) {
        Log.i("encryptKey", "About to encrypt " + plainKey + " with " + publicKey);
        this.plainKey = plainKey;
        this.publicKey = publicKey;
        PublicKey key0 = new KeyGenerator(context).getPublicKey();
        PublicKey key1 = new KeyGenerator(context).getPublicKey();
        Log.i("encryptKey", "Are the keys equal: " + key0.equals(key1));

        PublicKey key = new KeyGenerator(context).generateKeyFromString(publicKey);

        Base64Translator trans = new Base64Translator(context);
        byte [] transKeyBinary = trans.toBinary(plainKey);
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedBytes = cipher.doFinal(transKeyBinary);
            this.encryptedKey = trans.fromBinary(encryptedBytes);
            Log.i("encryptKey", "The encrypted key is " + encryptedKey);
        } catch (Exception e) {
            Log.e("encryptKey", e.toString(), e);
        }
        Log.i("encryptKey", "Finished encrypting key");
    }

    private void decryptKey(String encryptedKey, String privateKey) {
        Log.i("decryptKey", "About to decrypt " + encryptedKey + " with " + privateKey);
        this.encryptedKey = encryptedKey;
        this.privateKey = privateKey;

        PrivateKey key = new KeyGenerator(context).getPrivateKey();
        PrivateKey key1 = new KeyGenerator(context).getPrivateKey();
        Log.i("encryptKey", "Are the keys equal: " + key.equals(key1));
        Base64Translator trans = new Base64Translator(context);
        byte [] transKeyBinary = trans.toBinary(encryptedKey);
        byte [] decryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedBytes = cipher.doFinal(transKeyBinary);
            this.decryptedKey = trans.fromBinary(decryptedBytes);
            Log.i("decryptKey", "Decrypted Key: " + decryptedKey);
        }
        catch (Exception e) {
            Log.e("decryptKey", e.toString(), e);
        }
        Log.i("decryptKey", "Finished decrypting key");
    }

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
