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

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by 梁桂栋 on 2017/4/17.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
@AVClassName("Guide")
public class AVOGuide extends AVObject {


    public void setStatus(int status){
        put("status",status);
    }

    public int getStatus(){
        return getInt("status");
    }

    public AVOUser getCreator() {
        try {
            return getAVObject("creator",AVOUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCreator(AVOUser user) {
        super.put("creator", user);
    }

    public void setLocation(double latitude, double longitude) {
        put("location", new AVGeoPoint(latitude, longitude));
    }

    public AVGeoPoint getLocation() {
        return getAVGeoPoint("location");
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public void setPartner(List<AVOUser> partner) {
        put("partner", partner);
    }

    public List<AVOUser> getPartner() {
        return getList("partner", AVOUser.class);
    }
}
