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

package dong.lan.avoscloud;

import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import dong.lan.avoscloud.bean.AVOFavorite;
import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by 梁桂栋 on 17-3-31 ： 下午10:26.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public final class ModelConfig {
    private static final String API_ID = "Q0q6ba08Fh1dEX0VQhfPG8iK-9Nh9j0Va";
    private static final String API_KEY = "HnBQ9kCOze09FfQQMfVcEm8r";

    public static void init(Context appContext) {
        AVObject.registerSubclass(AVOLabel.class);
        AVObject.registerSubclass(AVOFeed.class);
        AVObject.registerSubclass(AVOFavorite.class);
        AVUser.alwaysUseSubUserClass(AVOUser.class);
        AVObject.registerSubclass(AVOFeedImage.class);
        AVObject.registerSubclass(AVOGuide.class);
        AVOSCloud.initialize(appContext, API_ID, API_KEY);
        AVOSCloud.setDebugLogEnabled(true);
        Realm.init(appContext);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("mapfun")
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
