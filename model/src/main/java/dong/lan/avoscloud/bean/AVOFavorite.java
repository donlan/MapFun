
package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 */
@AVClassName("Favorite")
public class AVOFavorite extends AVObject {

    public AVOUser getOwner() {
        try {
            return getAVObject("owner",AVOUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOwner(AVOUser user) {
        super.put("owner", user);
    }


    public void setFeed(AVOFeed feed) {
        put("feed", feed);
    }

    public AVOFeed getFeed() {
        try {
            return getAVObject("feed", AVOFeed.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
