
package dong.lan.mapfun.event;

import java.util.List;

import dong.lan.avoscloud.bean.AVOLabel;

/**
 * 用于通过EventBus传递标签
 */

public class PickLabelEvent {

    public List<AVOLabel> labels;

    public PickLabelEvent(List<AVOLabel> labels) {
        this.labels = labels;
    }
}
