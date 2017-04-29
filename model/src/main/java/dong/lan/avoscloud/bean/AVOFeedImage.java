
package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

@AVClassName("FeedImage")
public class AVOFeedImage extends AVObject {

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

    public void setFeed(AVOFeed avoFeed){
        put("feed",avoFeed);
    }

    public AVOFeed getFeed(){
        try {
            return getAVObject("feed",AVOFeed.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AVFile getImage() {
        return super.getAVFile("image");
    }

    public void setImage(AVFile file) {
        super.put("image", file);
    }
}
