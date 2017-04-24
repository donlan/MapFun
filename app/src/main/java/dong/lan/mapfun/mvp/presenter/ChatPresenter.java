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

package dong.lan.mapfun.mvp.presenter;

import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
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

/**
 * Created by 梁桂栋 on 2017/4/24.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
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
                    view.toast(textMessage.getText());
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

        AVOUser me = AVOUser.getCurrentUser(AVOUser.class);
        AVOGuide avoGuide = new AVOGuide();
        avoGuide.setCreator(me);
        avoGuide.setAddress(address);
        avoGuide.setStatus(dong.lan.base.ui.base.Config.GUIDE_STATUS_CREATED);
        avoGuide.setLocation(latitude, longitude);
        avoGuide.setPartner(Arrays.asList(me, targetUser));
        avoGuide.saveEventually(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {

                } else {
                    view.dialog("创建会话失败，错误码：" + e.getCode());
                }
            }
        });
    }

    //创建聊天会话
    private void init() {
        view.initView(targetUser.getUsername());

        AVOUser me = AVUser.getCurrentUser(AVOUser.class);
        App.myApp().getAvimClient().createConversation(Arrays.asList(me.getObjectId(), targetUser.getObjectId()),
                me.getUsername() + "&" + targetUser.getUsername(),
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
                                        view.toast(list.size() + "");
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
