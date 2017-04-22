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

package dong.lan.mapfun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
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
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.base.ui.Dialog;
import dong.lan.library.LabelTextView;
import dong.lan.map.activity.PickLocationActivity;
import dong.lan.map.service.Config;
import dong.lan.mapfun.App;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.ChatAdapter;

public class ChatActivity extends BaseBarActivity implements View.OnClickListener {

    private SwipeRefreshLayout refreshLayout;
    private LinearLayout chatToolLayout;
    private LabelTextView actionGuide;
    private RecyclerView chatList;
    private LinearLayout chatInputLayout;
    private EditText chatInput;
    private Button sendBtn;
    private ImageButton chatToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatList = (RecyclerView) findViewById(R.id.chatList);
        chatInput = (EditText) findViewById(R.id.chat_input);
        chatInputLayout = (LinearLayout) findViewById(R.id.chat_input_layout);
        sendBtn = (Button) findViewById(R.id.chat_send);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatRefresher);
        chatToolLayout = (LinearLayout) findViewById(R.id.chat_tool_layout);
        actionGuide = (LabelTextView) findViewById(R.id.chat_action_guide);
        chatToggle = (ImageButton) findViewById(R.id.chat_panel_toggle);
        chatToggle.setOnClickListener(this);
        actionGuide.setOnClickListener(this);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });


        String userStr = getIntent().getStringExtra(dong.lan.base.ui.base.Config.INTENT_USER);
        if (TextUtils.isEmpty(userStr)) {
            finish();
        } else {
            try {
                targetUser = (AVOUser) AVObject.parseAVObject(userStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (targetUser == null)
                finish();
            else {
                init();
            }
        }

    }


    private AVIMConversation conversation;
    private ChatAdapter adapter;

    private void init() {
        adapter = new ChatAdapter();
        chatList.setAdapter(adapter);

        tittle(targetUser.getUsername());

        AVOUser me = AVUser.getCurrentUser(AVOUser.class);
        App.myApp().getAvimClient().createConversation(Arrays.asList(me.getObjectId(), targetUser.getObjectId()),
                me.getUsername() + "&" + targetUser.getUsername(),
                null, false, true, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (e == null) {
                            toast(avimConversation.getName());
                            conversation = avimConversation;
                            conversation.queryMessages(100, new AVIMMessagesQueryCallback() {
                                @Override
                                public void done(List<AVIMMessage> list, AVIMException e) {
                                    if (e == null || list != null) {
                                        adapter.newMessage(list);
                                    }
                                }
                            });
                        } else {
                            dialog("创建会话失败，错误码：" + e.getCode());
                            finish();
                        }
                    }
                });

    }

    private AVOUser targetUser;

    private void sendTextMessage() {
        if (conversation == null) {
            toast("无效的会话");
            return;
        }
        String str = chatInput.getText().toString();
        if (TextUtils.isEmpty(str)) {
            toast("空消息");
            return;
        }
        final AVIMTextMessage textMessage = new AVIMTextMessage();
        textMessage.setText(str);
        conversation.sendMessage(textMessage, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e == null) {
                    toast(textMessage.getText());
                    adapter.newMessage(textMessage);
                } else {
                    dialog("发送消息失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.chat_panel_toggle:
                if (chatToolLayout.getVisibility() == View.GONE) {
                    chatToolLayout.setVisibility(View.VISIBLE);
                } else {
                    chatToolLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.chat_action_guide:
                startActivityForResult(new Intent(this, PickLocationActivity.class), Config.RESULT_LOCATION);
                break;
        }
    }


    private double latitude;
    private double longitude;
    private String address;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回的地理位置信息
        if (requestCode == Config.RESULT_LOCATION && resultCode == Config.RESULT_LOCATION) {
            address = data.getStringExtra(Config.LOC_ADDRESS);
            latitude = data.getDoubleExtra(Config.LATITUDE, 0);
            longitude = data.getDoubleExtra(Config.LONGITUDE, 0);
            new Dialog(this)
                    .setMessageText("确定发起协同定位导航？")
                    .setClickListener(new Dialog.DialogClickListener() {
                        @Override
                        public boolean onDialogClick(int which) {
                            if (which == Dialog.CLICK_RIGHT) {
                                newGuideAction();
                            }
                            return true;
                        }
                    }).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void newGuideAction() {
        if (conversation == null) {
            toast("无效的会话");
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
                    adapter.newMessage(locationMessage);
                    createPartnerGuide();
                } else {
                    dialog("发起协同定位失败，错误码：" + e.getCode());
                }
            }
        });
    }

    private void createPartnerGuide() {

        AVOUser me = AVUser.getCurrentUser(AVOUser.class);
        App.myApp().getAvimClient().createConversation(Arrays.asList(me.getObjectId(), targetUser.getObjectId()),
                me.getUsername() + " + " + targetUser.getUsername(),
                null, false, false, new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (e == null) {
                            AVOUser me = AVOUser.getCurrentUser(AVOUser.class);
                            AVOGuide avoGuide = new AVOGuide();
                            avoGuide.setCreator(me);
                            avoGuide.setConversationId(avimConversation.getConversationId());
                            avoGuide.setAddress(address);
                            avoGuide.setStatus(dong.lan.base.ui.base.Config.GUIDE_STATUS_CREATED);
                            avoGuide.setLocation(latitude, longitude);
                            avoGuide.setPartner(Arrays.asList(me, targetUser));
                            avoGuide.saveEventually();
                        } else {
                            dialog("创建会话失败，错误码：" + e.getCode());
                        }
                    }
                });
    }


    private MyMessageHandler myMessageHandler;

    @Override
    protected void onResume() {
        super.onResume();
        if (myMessageHandler == null)
            myMessageHandler = new MyMessageHandler();
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, myMessageHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, myMessageHandler);
    }


    private class MyMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client) {
            if (conversation != null && avimConversation.getConversationId().equals(conversation.getConversationId())) {
                adapter.newMessage(message);
            }
            super.onMessage(message, avimConversation, client);
        }
    }
}
