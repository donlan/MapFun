
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
import io.realm.Realm;
import io.realm.RealmConfiguration;



public final class ModelConfig {
    private static final String API_ID = "Q0q6ba08Fh1dEX0VQhfPG8iK-9Nh9j0Va";
    private static final String API_KEY = "HnBQ9kCOze09FfQQMfVcEm8r";

    public static void init(Context appContext) {
        AVObject.registerSubclass(AVOLabel.class);
        AVObject.registerSubclass(AVOFeed.class);
        AVObject.registerSubclass(AVOFavorite.class);
        AVObject.registerSubclass(AVOFeedImage.class);
        AVObject.registerSubclass(AVOGuide.class);
        AVObject.registerSubclass(AVOUser.class);
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
