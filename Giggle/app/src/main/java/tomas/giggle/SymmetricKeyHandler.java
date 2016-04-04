package tomas.giggle;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SymmetricKeyHandler {


    private Context context;

    private byte[] binaryDataKey;


    /**
     * Constructor
     *
     * @param context: the context
     */
    public SymmetricKeyHandler(Context context) {
        this.context = context;
        this.binaryDataKey = generateNewSymmetricKey();
    }


    /**
     * generate a new symmetric key
     *
     * @return the symmetric key as an array of bytes
     */
    public byte[] generateNewSymmetricKey() {
        Log.i("generateNewSymmetricKey", "About to generate a new symmetric key");
        try {
            // 1. Generate a session key
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey sessionKey = keyGen.generateKey();
            SecretKey secretKeySpec = new SecretKeySpec(sessionKey.getEncoded(), "AES");

            // 2. Encrypt the session key with the RSA public key
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, new tomas.giggle.KeyGenerator(context).getPublicKey());
            byte[] encryptedSessionKey = rsaCipher.doFinal(secretKeySpec.getEncoded());

            String stringOfBinary = Base64.encodeToString(encryptedSessionKey, Base64.DEFAULT);
            Log.i("generateNewSymmetricKey", "Generated new symmetric key: " + stringOfBinary);
            return encryptedSessionKey;
        } catch (Exception e) {
            Log.e("generateNewSymmetricKey", e.toString(), e);
        }
        Log.i("generateNewSymmetricKey", "Symmetric key not generated, returning null");
        return null;
    }


    // GETTERS


    public byte[] getBinaryDataKey() {
        return binaryDataKey;
    }

}
