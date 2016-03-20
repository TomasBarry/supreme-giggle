package tomas.giggle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyGenerator extends Activity{

    SharedPreferences SP;
    SharedPreferences.Editor SPE;
    PublicKey publicKey;
    PrivateKey privateKey;
    Context context;

    public KeyGenerator(Context context){
        this.context = context;
        SP = context.getSharedPreferences("KeyPair", MODE_PRIVATE);
    }

    public void generateKeys(){
        try {
            KeyPairGenerator generator;
            generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(256, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();
            publicKey = pair.getPublic();
            privateKey = pair.getPrivate();
            byte[] publicKeyBytes = publicKey.getEncoded();
            String pubKeyStr = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));
            byte[] privKeyBytes = privateKey.getEncoded();
            String privKeyStr = new String(Base64.encode(privKeyBytes, Base64.DEFAULT));
            SPE = SP.edit();
            SPE.putString("PublicKey", pubKeyStr);
            SPE.putString("PrivateKey", privKeyStr);
            SPE.apply();
        } catch (Exception e) {
            Log.e("generateKeys", "Exception: " + e.toString(), e);
        }
    }
    public PublicKey getPublicKey(){
        String pubKeyStr = SP.getString("PublicKey", "");
        byte[] sigBytes = Base64.decode(pubKeyStr, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (Exception e) {
            Log.e("getPublicKey", "Exception: " + e.toString(), e);
        }
        try {
            assert keyFact != null;
            return  keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getPublicKeyAsString(){
        return SP.getString("PublicKey", "");
    }
    public PrivateKey getPrivateKey(){
        String privKeyStr = SP.getString("PrivateKey", "");
        byte[] sigBytes = Base64.decode(privKeyStr, Base64.DEFAULT);
        PKCS8EncodedKeySpec PKCS8EncodedKey = new PKCS8EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (Exception e) {
            Log.e("getPrivateKey", "Exception: " + e.toString(), e);
        }
        try {
            assert keyFact != null;
            return keyFact.generatePrivate(PKCS8EncodedKey);
        } catch (Exception e) {
            Log.e("getPrivateKey", "Exception: " + e.toString(), e);
        }
        return null;
    }
    public String getPrivateKeyAsString(){
        return SP.getString("PrivateKey", "");
    }
}