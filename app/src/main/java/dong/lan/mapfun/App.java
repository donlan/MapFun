

package dong.lan.mapfun;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.blankj.ALog;

import dong.lan.avoscloud.ModelConfig;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.utils.SPHelper;
import dong.lan.map.service.LocationService;
import dong.lan.mapfun.im.IMMessageHandler;

/**
 */

public class App extends MultiDexApplication {
    private static App app;
    private AVIMClient avimClient;

    public static App myApp() {
        return app;
    }

    public LocationService getLocationService() {
        return LocationService.service();
    }

    public AVIMClient getAvimClient() {
        return avimClient;
    }



    public void initIM(){
        AVIMMessageManager.registerDefaultMessageHandler(new IMMessageHandler());

        AVIMClient.getInstance(AVOUser.getCurrentUser().getObjectId())
                .open(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            App.this.avimClient = avimClient;
                        } else {
                            Toast.makeText(App.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        LocationService.service().init(this);
        ModelConfig.init(this);
        SPHelper.instance().init(this,"");
        new ALog.Builder(this).setGlobalTag("DOOZE");

    }
}
