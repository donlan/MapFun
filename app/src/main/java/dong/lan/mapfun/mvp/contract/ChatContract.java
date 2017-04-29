
package dong.lan.mapfun.mvp.contract;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

import java.util.List;

import dong.lan.base.ui.IActivityFunc;
import dong.lan.base.ui.ProgressView;

/**
 */

public interface ChatContract {
    public interface View extends ProgressView, IActivityFunc {
        void initView(String username);

        void showMessage(List<AVIMMessage> list);

        void newMessage(AVIMMessage message);
    }

    public interface Presenter {
        void start(String userSeq);

        void sendTextMessage(String content);

        void newGuide(double latitude, double longitude, String address);

        void handlerMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client);
    }

    public interface Model {
    }
}
