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

package dong.lan.mapfun.helper;

import android.content.Context;
import android.view.View;

import com.avos.avoscloud.AVUser;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.customView.MapPinNumView;

/**
 * Created by 梁桂栋 on 2017/4/16.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
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
        AVOUser me = AVUser.getCurrentUser(AVOUser.class);
        if(me.equals(feed.getCreator())){
            pinNumView = new MapPinNumView(context,feed.getContent().substring(0,2),0xff2ecc71,18,0xffffffff);
        }else{
            pinNumView = new MapPinNumView(context,feed.getContent().substring(0,2),0xffe67e22,18,0xffffffff);
        }

        return pinNumView;
    }
}
