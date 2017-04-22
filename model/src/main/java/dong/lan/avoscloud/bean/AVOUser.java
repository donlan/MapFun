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

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

/**
 * Created by 梁桂栋 on 2017/4/12.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class AVOUser extends AVUser {


    public boolean isShareLocation(){
        return getBoolean("shareLoc");
    }

    public void setShareLocation(boolean shareLocation){
        put("shareLoc",shareLocation);
    }

    public String nickname(){
        return getString("nickname");
    }

    public void setNickname(String nickname){
        put("nickname",nickname);
    }


    public void setSex(int sex){
        put("sex",sex);
    }

    public int getSex(){
        return getInt("sex");
    }
    public AVFile getAvatar() {
        return super.getAVFile("avatar");
    }

    public void setAvatar(AVFile file) {
        super.put("avatar", file);
    }

    public void setLastLocation(double latitude,double longitude){
        put("lastLocation",new AVGeoPoint(latitude,longitude));
    }

    public AVGeoPoint getLastLocation(){
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
        this.saveInBackground();
    }

}
