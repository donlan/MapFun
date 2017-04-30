
package dong.lan.mapfun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
 * 聊天适配器
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int TYPE_FROM = 1000;
    private static final int TYPE_SEND = 2000;
    private List<AVIMMessage> messages;
    private String me;

    public ChatAdapter() {
        messages = new ArrayList<>();
        me = AVOUser.getCurrentUser().getObjectId();
    }

    public ChatAdapter(List<AVIMMessage> messages) {
        this.messages = messages;
        me = AVOUser.getCurrentUser().getObjectId();
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
        return me.equals(messages.get(position).getFrom()) ? TYPE_SEND : TYPE_FROM;
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
            head = (CircleImageView) itemView.findViewById(R.id.chat_head);
            text = (TextView) itemView.findViewById(R.id.chat_text);
            chatTime = (TextView) itemView.findViewById(R.id.chat_time);
        }
    }
}
