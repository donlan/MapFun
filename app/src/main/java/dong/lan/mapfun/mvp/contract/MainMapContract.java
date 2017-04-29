
package dong.lan.mapfun.mvp.contract;

import com.baidu.mapapi.map.Marker;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.IActivityFunc;
import dong.lan.base.ui.ProgressView;

/**
 */

public interface MainMapContract {
    public interface View extends ProgressView,IActivityFunc {
        void showNearUser(List<AVOUser> users);

        void showNearFeed(List<AVOFeed> feeds);
    }

    public interface Presenter {
        void saveUserLocation();

        void queryNearFeed();

        void queryNearUser();

        void queryNearByLabel(List<AVOLabel> labels);

        boolean handlerMarkerClick(Marker marker);

        void saveShareLocation(boolean isShare);
    }

    public interface Model {
    }
}
