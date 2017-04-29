
package dong.lan.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 */

public class SPHelper {
    private SharedPreferences preferences;

    private static final String DEFAULT_SP_NAME = "mapfun";

    private static SPHelper spHelper = null;

    public static SPHelper instance() {
        if (spHelper == null)
            spHelper = new SPHelper();
        return spHelper;
    }

    public void init(Context context, String spName) {
        preferences = context.getSharedPreferences(
                TextUtils.isEmpty(spName) ? DEFAULT_SP_NAME : spName, Context.MODE_PRIVATE);
    }

    private SPHelper() {
    }


    public void putString(String key, String val) {
        if (key == null || val == null)
            return;
        preferences.edit().putString(key, val).commit();
    }

    public void putInt(String key, int val) {
        if (key == null)
            return;
        preferences.edit().putInt(key, val).apply();
    }

    public void putBoolean(String key, boolean val) {
        if (key == null)
            return;
        preferences.edit().putBoolean(key, val).apply();
    }


    public String getString(String key) {
        if (key == null)
            return "";
        return preferences.getString(key, "");
    }

    public int getInt(String key) {
        return preferences.getInt(key, -1);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
}
