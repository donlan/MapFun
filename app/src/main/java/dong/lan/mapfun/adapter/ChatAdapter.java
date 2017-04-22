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

package dong.lan.mapfun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.ArrayList;
import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.base.utils.DateUtils;
import dong.lan.mapfun.R;

/**
 * Created by 梁桂栋 on 2017/4/17.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int TYPE_FROM = 1000;
    private static final int TYPE_SEND = 2000;
    private List<AVIMMessage> messages;
    private String me;

    public ChatAdapter() {
        messages = new ArrayList<>();
        me = AVOUser.getCurrentUser(AVOUser.class).getObjectId();
    }

    public ChatAdapter(List<AVIMMessage> messages) {
        this.messages = messages;
        me = AVOUser.getCurrentUser(AVOUser.class).getObjectId();
    }

    public void newMessage(AVIMMessage message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void newMessage(List<AVIMMessage> message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        int s = messages.size() - 1;
        if (s < 0)
            s = 0;
        messages.addAll(message);
        int e = messages.size() - 1;
        notifyItemRangeInserted(s, e);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FROM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_from, null);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send, null);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getFrom().equals(me) ? TYPE_SEND : TYPE_FROM;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVIMMessage avimMessage = messages.get(position);
        if (avimMessage instanceof AVIMTextMessage) {
            AVIMTextMessage textMessage = (AVIMTextMessage) avimMessage;
            holder.text.setText(textMessage.getText());
            holder.chatTime.setText(DateUtils.getTimestampString(textMessage.getTimestamp()));
        } else if (avimMessage instanceof AVIMLocationMessage) {
            AVIMLocationMessage locationMessage = (AVIMLocationMessage) avimMessage;
            holder.text.setText(locationMessage.getText());
            holder.chatTime.setText(DateUtils.getTimestampString(locationMessage.getTimestamp()));
        }
        Toast.makeText(holder.itemView.getContext(),avimMessage.getContent(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView head;

        public TextView text;

        public TextView chatTime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.chat_head);
            itemView.findViewById(R.id.chat_text);
            itemView.findViewById(R.id.chat_time);
        }
    }
}
