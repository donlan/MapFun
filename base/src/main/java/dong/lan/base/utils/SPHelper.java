/*
 *   Copyright 2016, donlan(梁桂栋)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me: stonelavender@hotmail.com
 */

package dong.lan.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by 梁桂栋 on 2017/4/24.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
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
