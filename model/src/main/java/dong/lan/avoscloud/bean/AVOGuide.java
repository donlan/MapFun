
package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;

import java.util.List;

/**
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


    public String getConvId() {
        return getString("conv");
    }

    public void setConv(String conv) {
        put("conv", conv);
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
