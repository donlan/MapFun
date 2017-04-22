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

import com.avos.avoscloud.AVFile;
import com.bumptech.glide.Glide;

import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.mapfun.R;

/**
 * Created by 梁桂栋 on 2017/4/16.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {


    private List<AVOUser> users;
    private BaseItemClickListener<AVOUser> clickListener;

    public FriendsAdapter(List<AVOUser> users, BaseItemClickListener<AVOUser> itemClickListener) {
        this.users = users;
        clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fridens, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVFile avFile = users.get(position).getAvatar();
        Glide.with(holder.itemView.getContext())
                .load(avFile == null ? "" : avFile.getUrl())
                .error(R.drawable.head)
                .into(holder.avatar);
        holder.username.setText(users.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (CircleImageView) itemView.findViewById(R.id.item_friend_avatar);
            username = (TextView) itemView.findViewById(R.id.item_friend_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    if (clickListener != null)
                        clickListener.onClick(users.get(p), 0, p);
                }
            });

        }
    }
}
