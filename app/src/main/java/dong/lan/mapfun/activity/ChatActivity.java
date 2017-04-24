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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;

import java.util.List;

import dong.lan.base.ui.BaseBarActivity;
import dong.lan.base.ui.Dialog;
import dong.lan.library.LabelTextView;
import dong.lan.map.activity.PickLocationActivity;
import dong.lan.map.service.Config;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.ChatAdapter;
import dong.lan.mapfun.mvp.contract.ChatContract;
import dong.lan.mapfun.mvp.presenter.ChatPresenter;

/**
 * 聊天页面
 */

public class ChatActivity extends BaseBarActivity implements View.OnClickListener, ChatContract.View {

    private SwipeRefreshLayout refreshLayout;
    private LinearLayout chatToolLayout;
    private LabelTextView actionGuide;
    private RecyclerView chatList;
    private LinearLayout chatInputLayout;
    private EditText chatInput;
    private Button sendBtn;
    private ImageButton chatToggle;


    private ChatContract.Presenter presenter = null;

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
                presenter.sendTextMessage(chatInput.getText().toString());
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });

        presenter = new ChatPresenter(this);


        String userStr = getIntent().getStringExtra(dong.lan.base.ui.base.Config.INTENT_USER);

        presenter.start(userStr);

    }

    private ChatAdapter adapter;


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
                                presenter.newGuide(latitude, longitude, address);
                            }
                            return true;
                        }
                    }).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public Activity activity() {
        return this;
    }

    @Override
    public void initView(String username) {
        adapter = new ChatAdapter();
        chatList.setLayoutManager(new GridLayoutManager(this, 1));
        chatList.setAdapter(adapter);
        tittle(username);
    }

    @Override
    public void showMessage(List<AVIMMessage> list) {
        adapter.newMessage(list);
        chatList.scrollToPosition(list.size());
    }

    @Override
    public void newMessage(AVIMMessage textMessage) {
        adapter.newMessage(textMessage);
        chatList.scrollToPosition(chatList.getAdapter().getItemCount() - 1);
        chatInput.setText("");
    }


    private class MyMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client) {
            presenter.handlerMessage(message, avimConversation, client);
            super.onMessage(message, avimConversation, client);
        }
    }
}
