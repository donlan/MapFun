
package dong.lan.mapfun.mvp.presenter;

import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.mapfun.App;
import dong.lan.mapfun.mvp.contract.ChatContract;
import dong.lan.mapfun.uitls.StringUtils;

/**
 */

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View view;
    private AVOUser targetUser;
    private AVIMConversation conversation;

    public ChatPresenter(ChatContract.View view) {
        this.view = view;
    }

    @Override
    public void start(String userSeq) {
        if (TextUtils.isEmpty(userSeq)) {
            view.activity().finish();
        } else {
            try {
                targetUser = (AVOUser) AVObject.parseAVObject(userSeq);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (targetUser == null)
                view.activity().finish();
            else {
                init();
            }
        }
    }

    @Override
    public void sendTextMessage(String content) {
        if (conversation == null) {
            view.toast("无效的会话");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            view.toast("空消息");
            return;
        }
        final AVIMTextMessage textMessage = new AVIMTextMessage();
        textMessage.setText(content);
        conversation.sendMessage(textMessage, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    view.newMessage(textMessage);
                } else {
                    view.dialog("发送消息失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void newGuide(final double latitude, final double longitude, final String address) {
        if (conversation == null) {
            view.toast("无效的会话");
            return;
        }
        if(latitude == 0 || longitude == 0){
            view.toast("无效位置信息，请从地图中选择一个位置");
            return;
        }
        if(TextUtils.isEmpty(address)){
            view.toast("位置描述为空，请从地图中选择一个位置");
            return;
        }
        Map<String, Object> info = new HashMap<>(1);
        info.put("type", "guide");
        final AVIMLocationMessage locationMessage = new AVIMLocationMessage();
        locationMessage.setLocation(new AVGeoPoint(latitude, longitude));
        locationMessage.setText("发起了协同定位导航。\n目的地：" + address);
        locationMessage.setAttrs(info);
        conversation.sendMessage(locationMessage, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    view.newMessage(locationMessage);
                    createPartnerGuide(latitude, longitude, address);
                } else {
                    view.dialog("发起协同定位失败，错误码：" + e.getCode());
                }
            }
        });
    }

    //处理接受信息
    @Override
    public void handlerMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client) {
        if (conversation != null && avimConversation.getConversationId().equals(conversation.getConversationId())) {
            view.newMessage(message);
        }
    }

    private void createPartnerGuide(final double latitude, final double longitude, final String address) {


        App.myApp().getAvimClient().createConversation(conversation.getMembers(), "guide:"+conversation.getName(), null, false, true, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVIMException e) {
                if(e == null){
                    AVOUser me = AVOUser.getCurrentUser();
                    AVOGuide avoGuide = new AVOGuide();
                    avoGuide.setConv(avimConversation.getConversationId());
                    avoGuide.setCreator(me);
                    avoGuide.setAddress(address);
                    avoGuide.setPartnerInfo(StringUtils.partnerInfo(me,targetUser));
                    avoGuide.setStatus(dong.lan.base.ui.base.Config.GUIDE_STATUS_CREATED);
                    avoGuide.setLocation(latitude, longitude);
                    avoGuide.setPartner(Arrays.asList(me, targetUser));
                    avoGuide.saveEventually(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                view.toast("创建成功");
                            } else {
                                e.printStackTrace();
                                view.dialog("创建协同导航失败，错误码：" + e.getCode());
                            }
                        }
                    });
                }else{
                    e.printStackTrace();
                    view.dialog("创建协同导航失败，错误码：" + e.getCode());
                }
            }
        });
    }

    //创建聊天会话
    private void init() {
        view.initView(targetUser.getUserName());
        AVOUser me = AVOUser.getCurrentUser();
        App.myApp().getAvimClient().createConversation(Arrays.asList(me.getObjectId(), targetUser.getObjectId()),
                me.getUserName() + "&" + targetUser.getUserName(),
                null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (e == null) {
                            view.toast(avimConversation.getName());
                            conversation = avimConversation;
                            conversation.queryMessages(100, new AVIMMessagesQueryCallback() {
                                @Override
                                public void done(List<AVIMMessage> list, AVIMException e) {
                                    if (e == null || list != null) {
                                        view.showMessage(list);
                                    }
                                }
                            });
                        } else {
                            view.dialog("创建会话失败，错误码：" + e.getCode());
                            view.activity().finish();
                        }
                    }
                });

    }

}
