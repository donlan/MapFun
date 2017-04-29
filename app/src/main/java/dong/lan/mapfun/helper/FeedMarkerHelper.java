package dong.lan.mapfun.helper;

import android.content.Context;
import android.view.View;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.customView.MapPinNumView;

/**
 */

public class FeedMarkerHelper {

    private static FeedMarkerHelper helper;

    private FeedMarkerHelper(){}

    public static FeedMarkerHelper instance(){
        if(helper == null)
            helper = new FeedMarkerHelper();
        return helper;
    }

    public  View formMarkerView(Context context,AVOFeed feed){
        MapPinNumView pinNumView = null;
        AVOUser me = AVOUser.getCurrentUser();
        if(me.equals(feed.getCreator())){
            pinNumView = new MapPinNumView(context,feed.getContent().substring(0,2),0xff2ecc71,18,0xffffffff);
        }else{
            pinNumView = new MapPinNumView(context,feed.getContent().substring(0,2),0xffe67e22,18,0xffffffff);
        }

        return pinNumView;
    }
}
