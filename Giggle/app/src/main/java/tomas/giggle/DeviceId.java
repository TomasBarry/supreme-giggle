package tomas.giggle;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;


public class DeviceId {

    private String deviceIdString;
    private Context context;

    public DeviceId(Context context) {
        this.context = context;
        this.deviceIdString = getUniqueAndroidDeviceId();
    }

    /**
     * getUniqueAndroidDeviceId
     * <p/>
     * Calculate the unique device ID and return it
     *
     * @return a String representing a unique ID for the device
     */
    private String getUniqueAndroidDeviceId() {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("getUniqueAndroidDevice", "tm created");
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Log.i("getUniqueAndroidDevice", "strings created");

        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        Log.i("Giggle_getUniqueAndroid", "UUID created");
        return deviceUuid.toString();
    }

    public String getDeviceIdString() {
        return this.deviceIdString;
    }
}
