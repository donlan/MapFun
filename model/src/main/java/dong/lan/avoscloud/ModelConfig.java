
package dong.lan.avoscloud;

import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

import dong.lan.avoscloud.bean.AVOFavorite;
import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;



public final class ModelConfig {
    private static final String API_ID = "O100DaDB6semiO8VkJG1DRee-9Nh9j0Va";
    private static final String API_KEY = "EDFaaSeztWiki8azJb9p6R6M";

    public static void init(Context appContext) {
        AVObject.registerSubclass(AVOLabel.class);
        AVObject.registerSubclass(AVOFeed.class);
        AVObject.registerSubclass(AVOFavorite.class);
        AVObject.registerSubclass(AVOFeedImage.class);
        AVObject.registerSubclass(AVOGuide.class);
        AVObject.registerSubclass(AVOUser.class);
        AVOSCloud.initialize(appContext, API_ID, API_KEY);
        AVOSCloud.setDebugLogEnabled(true);
        //Realm.init(appContext);
//        RealmConfiguration configuration = new RealmConfiguration.Builder()
//                .deleteRealmIfMigrationNeeded()
//                .name("mapfun")
//                .build();
//        Realm.setDefaultConfiguration(configuration);
    }
}
