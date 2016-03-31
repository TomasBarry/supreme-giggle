package tomas.giggle;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

/**
 * Created by Tomas on 30/03/2016.
 */
public class Base64Translator {

    private Context context;

    public Base64Translator(Context context) {
        Log.i("Base64Translator", "Beggining constructor");
        this.context = context;
        Log.i("Base64Translator", "Finishing constructor");
    }

    public String fromBinary(byte [] binaryData) {
        Log.i("fromBinary", "About to convert binaryData");
        String stringData = Base64.encodeToString(binaryData, Base64.DEFAULT);
        Log.i("fromBinary", "Converted binaryData to " + stringData);
        return stringData;
    }

    public byte [] toBinary(String stringData) {
        Log.i("toBinary", "About to convert stringData " + stringData);
        byte [] binaryData = Base64.decode(stringData, Base64.DEFAULT);
        Log.i("toBinary", "Converted stringData");
        return binaryData;
    }
}
