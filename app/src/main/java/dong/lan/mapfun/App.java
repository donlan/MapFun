
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

package dong.lan.mapfun;

import android.app.Application;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import dong.lan.avoscloud.ModelConfig;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.map.service.LocationService;
import dong.lan.mapfun.im.IMMessageHandler;

/**
 * Created by 梁桂栋 on 2017/4/11.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class App extends Application {
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
    public void onCreate() {
        super.onCreate();
        app = this;
        LocationService.service().init(this);
        ModelConfig.init(this);

    }
}
