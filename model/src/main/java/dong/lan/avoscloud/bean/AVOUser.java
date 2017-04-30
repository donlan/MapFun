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

package dong.lan.avoscloud.bean;

import android.text.TextUtils;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;

import dong.lan.base.utils.SPHelper;

@AVClassName("MyUser")
public class AVOUser extends AVObject {


    public AVUser getCreator() {
        return getAVUser("user");
    }

    public void setCreator(AVUser user) {
        super.put("user", user);
    }

    public void setCreator(AVOUser user) {
        super.put("user", user.getCreator());
    }

    public boolean isShareLocation() {
        return getBoolean("shareLoc");
    }

    public void setShareLocation(boolean shareLocation) {
        put("shareLoc", shareLocation);
    }

    public String nickname() {
        return getString("nickname");
    }

    public void setNickname(String nickname) {
        put("nickname", nickname);
    }


    public String getUserName() {
        return getCreator().getUsername();
    }

    public void setUsername(String username) {
        getCreator().setUsername(username);
    }


    public String getMobile() {
        return getCreator().getMobilePhoneNumber();
    }

    public void setMobile(String mobile) {
        getCreator().setMobilePhoneNumber(mobile);
    }

    public void setSex(int sex) {
        put("sex", sex);
    }

    public int getSex() {
        return getInt("sex");
    }

    public AVFile getAvatar() {
        return getAVFile("avatar");
    }

    public void setAvatar(AVFile file) {
        super.put("avatar", file);
    }

    public void setLastLocation(double latitude, double longitude) {
        put("lastLocation", new AVGeoPoint(latitude, longitude));
    }

    public AVGeoPoint getLastLocation() {
        return getAVGeoPoint("lastLocation");
    }


    public AVRelation getFriends() {
        return getRelation("friends");
    }

    public void removeFriend(AVOUser avoUser) {
        getFriends().remove(avoUser);
        this.saveInBackground();
    }

    public void addFriend(AVOUser avoUser) {
        getFriends().add(avoUser);
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null)
                    e.printStackTrace();
            }
        });
    }

    public static void logInInBackground(String username, String password, LogInCallback<AVUser> logInCallback) {
        AVUser.logInInBackground(username, password, logInCallback);
    }


    private static AVOUser user;

    public static void setCurrentUser(AVOUser avoUser) {
        user = avoUser;
        SPHelper.instance().putString("user", avoUser.toString());
    }

    public static AVOUser getCurrentUser() {
        if (user == null) {
            String str = SPHelper.instance().getString("user");
            try {
                user = (AVOUser) AVObject.parseAVObject(str);
                return user;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return user;
        }
    }

    public static void logOut() {
        user = null;
        AVUser.logOut();
        SPHelper.instance().putString("user", "");
    }

    public String getDisplayName() {
        if (TextUtils.isEmpty(nickname())) {
            return getUserName();
        }
        return nickname();
    }
}
