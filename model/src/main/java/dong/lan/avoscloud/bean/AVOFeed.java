
package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

/**
 */


@AVClassName("Feed")
public class AVOFeed extends AVObject {

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
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e != null)
                    e.printStackTrace();
            }
        });
    }

    public void addLike(AVOUser tourist) {
        getLikes().add(tourist);
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e!=null)
                    e.printStackTrace();
            }
        });
    }
}
