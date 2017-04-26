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

package dong.lan.mapfun.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Marker;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.map.service.LocationService;
import dong.lan.mapfun.App;
import dong.lan.mapfun.activity.FeedDetailActivity;
import dong.lan.mapfun.activity.UserCenterActivity;
import dong.lan.mapfun.mvp.contract.MainMapContract;

/**
 * Created by 梁桂栋 on 2017/4/15.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class MainMapPresenter implements MainMapContract.Presenter {

    private MainMapContract.View view;

    public MainMapPresenter(MainMapContract.View view) {
        this.view = view;
    }

    @Override
    public void saveUserLocation() {
        App.myApp().getLocationService().unregisterCallback(this);
        BDLocation point = App.myApp().getLocationService().getLastLocation();
        if (point != null) {
            AVOUser user = AVOUser.getCurrentUser();
            user.setLastLocation(point.getLatitude(), point.getLongitude());
            user.saveEventually();
        }
    }

    @Override
    public void queryNearFeed() {
        AVQuery<AVOFeed> query = new AVQuery<>("Feed");
        BDLocation location = LocationService.service().getLastLocation();
        AVGeoPoint point = new AVGeoPoint();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        query.whereWithinKilometers("location", point, 10);
        query.limit(100);
        query.include("labels");
        query.whereEqualTo("isPublic", true);
        query.findInBackground(new FindCallback<AVOFeed>() {
            @Override
            public void done(List<AVOFeed> list, AVException e) {
                if (e == null) {
                    view.toast("附近有 " + list.size() + " 个图趣");
                    view.showNearFeed(list);
                } else {
                    view.dialog("获取附近的图趣失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void queryNearUser() {
        BDLocation location = LocationService.service().getLastLocation();
        if (location == null) {
            view.toast("无法获取当前位置信息");
            return;
        }
        AVQuery<AVOUser> query = new AVQuery<>("MyUser");
        AVGeoPoint point = new AVGeoPoint();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        query.whereWithinKilometers("lastLocation", point, 10);
        query.limit(100);
        query.include("user");
        query.whereEqualTo("shareLoc",true);
        query.whereNotEqualTo("objectId",AVOUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<AVOUser>() {
            @Override
            public void done(List<AVOUser> list, AVException e) {
                if (e == null) {
                    view.toast("附近有 " + list.size() + " 个趣友");
                    view.showNearUser(list);
                } else {
                    view.dialog("找不到附近的用户，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void queryNearByLabel(final List<AVOLabel> labels) {
        AVQuery<AVOFeed> query = new AVQuery<>("Feed");
        BDLocation location = LocationService.service().getLastLocation();
        AVGeoPoint point = new AVGeoPoint();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        query.whereContainedIn("labels", labels);
        query.include("labels");
        query.whereWithinKilometers("location", point, 10);
        query.whereEqualTo("isPublic", true);
        query.limit(100);
        query.findInBackground(new FindCallback<AVOFeed>() {
            @Override
            public void done(List<AVOFeed> list, AVException e) {
                if (e == null) {
                    view.toast("搜索到附近 " + list.size() + " 个图趣");
                    view.showNearFeed(list);
                } else {
                    view.dialog("获取附近的图趣失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public boolean handlerMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        if (bundle != null) {
            int type = bundle.getInt("type", -1);
            if (type == 0) { //点击的marker是用户头像
                String data = bundle.getString("user");
                AVOUser avoUser = null;
                try {
                    avoUser = (AVOUser) AVOUser.parseAVObject(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (avoUser != null) {
                    Intent intent = new Intent(view.activity(), UserCenterActivity.class);
                    intent.putExtra("userSeq", data);
                    view.activity().startActivity(intent);
                }
            } else if (type == 1) {// 点击的是图趣
                String data = bundle.getString("feed");
                AVOFeed feed = null;
                try {
                    feed = (AVOFeed) AVOFeed.parseAVObject(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (feed != null) {
                    Intent intent = new Intent(view.activity(), FeedDetailActivity.class);
                    intent.putExtra("feed", data);
                    view.activity().startActivity(intent);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void saveShareLocation(boolean isShare) {
        AVOUser user = AVOUser.getCurrentUser();
        user.setShareLocation(isShare);
        user.saveEventually();
    }
}
