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
import com.avos.avoscloud.AVRelation;

import java.util.List;

/**
 * Created by 梁桂栋 on 2017/4/12.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */


@AVClassName("Feed")
public class AVOFeed extends AVObject {

    public AVOUser getCreator() {
        return super.getAVUser("creator", AVOUser.class);
    }

    public void setCreator(AVOUser user) {
        super.put("creator", user);
    }


    public void setLabel(List<AVOLabel> label) {
        put("labels", label);
    }

    public List<AVOLabel> getLabel() {
        try {
            return getList("labels", AVOLabel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPublic() {
        return getBoolean("isPublic");
    }

    public void setPublic(boolean isPublic) {
        put("isPublic", isPublic);
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public void setLocation(double latitude, double longitude) {
        put("location", new AVGeoPoint(latitude, longitude));
    }

    public AVGeoPoint getLocation() {
        return getAVGeoPoint("location");
    }

    public AVRelation getLikes() {
        return getRelation("likes");
    }

    public void removeLike(AVOUser tourist) {
        getLikes().remove(tourist);
        this.saveInBackground();
    }

    public void addLike(AVOUser tourist) {
        getLikes().add(tourist);
        this.saveInBackground();
    }
}
