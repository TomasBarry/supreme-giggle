package tomas.giggle;

import android.content.Context;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Tomas on 30/03/2016.
 */
public class SymmetricKeyHandler {

    private Context context;
    private SecretKey secretKey;
    private SecretKeySpec secretKeySpec;

    private byte [] binaryDataKey;

    public SymmetricKeyHandler(Context context) {
        this.context = context;
        this.binaryDataKey = generateNewSymmetricKey();
    }

    public SymmetricKeyHandler(Context context, byte [] binaryDataKey) {
        this.context = context;
        this.binaryDataKey = binaryDataKey;
    }

    public byte [] generateNewSymmetricKey() {
        Log.i("generateNewSymmetricKey", "About to generate a new symmetric key");
        try {
            // 1. Generate a session key
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey sessionKey = keyGen.generateKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKey.getEncoded(), "AES");

            // 2. Encrypt the session key with the RSA public key
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, new tomas.giggle.KeyGenerator(context).getPublicKey());
            byte [] encryptedSessionKey = rsaCipher.doFinal(secretKeySpec.getEncoded());

            Base64Translator trans = new Base64Translator(context);
            String stringOfBinary = trans.fromBinary(encryptedSessionKey);
            Log.i("generateNewSymmetricKey", "Generated new symmetric key: " + stringOfBinary);
            return encryptedSessionKey;
        } catch (Exception e) {
            Log.e("generateNewSymmetricKey", e.toString(), e);
        }
        Log.i("generateNewSymmetricKey", "Symmetric key not generated, returning null");
        return null;
    }

    public byte[] getBinaryDataKey() {
        return binaryDataKey;
    }

    public SecretKeySpec getSecretKeySpec() {
        return secretKeySpec;
    }
}
