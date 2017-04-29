
package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import dong.lan.base.ui.customView.ItemLabelData;

/**
 */
@AVClassName("Label")
public class AVOLabel extends AVObject implements ItemLabelData{

    public String getLabel(){
        return getString("label");
    }

    public void setLabel(String label){
        put("label",label);
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

    @Override
    public String labelText() {
        return getLabel();
    }
}
