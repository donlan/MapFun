
package dong.lan.im;

import android.content.Context;
import android.util.Log;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 */

public class IMConfig {

    private static final String TAG = IMConfig.class.getSimpleName();

    public static void init(Context appContext){
        JMessageClient.init(appContext);
        JMessageClient.setDebugMode(true);
    }

    public static void logout(){
        JMessageClient.logout();
    }

    public static void register(String username,String password){
        JMessageClient.register(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: "+i+"->"+s);
            }
        });
    }

    public static void login(String username,String password){
        JMessageClient.login(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: "+i+"->"+s);
            }
        });
    }
}
