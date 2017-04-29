
package dong.lan.avoscloud.realm;

import dong.lan.base.ui.customView.ItemLabelData;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Label extends RealmObject implements ItemLabelData {

    @PrimaryKey
    private String objId;
    private String label;
    private User creator;

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public String labelText() {
        return label;
    }
}
