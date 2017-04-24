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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.base.BaseItemClickListener;
import dong.lan.library.LabelTextView;
import dong.lan.mapfun.R;

/**
 * Created by 梁桂栋 on 2017/4/16.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 * 内容列表的适配器
 */

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder> {


    private List<AVOFeed> feeds;
    private BaseItemClickListener<AVOFeed> clickListener;

    public FeedsAdapter(List<AVOFeed> feeds, BaseItemClickListener<AVOFeed> itemClickListener) {
        this.feeds = feeds;
        clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVOFeed feed = feeds.get(position);
        List<AVOLabel> labels = feed.getLabel();
        StringBuilder sb = new StringBuilder();
        for (AVOLabel label : labels) {
            sb.append("#");
            sb.append(label.getLabel());
            sb.append("  ");
        }
        holder.labels.setText(sb.toString());
        holder.content.setText(feeds.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return feeds == null ? 0 : feeds.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LabelTextView labels;
        ImageView image;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);

            labels = (LabelTextView) itemView.findViewById(R.id.item_feed_label);
            image = (ImageView) itemView.findViewById(R.id.item_feed_image);
            content = (TextView) itemView.findViewById(R.id.item_feed_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    if (clickListener != null)
                        clickListener.onClick(feeds.get(p), 0, p);
                }
            });

        }
    }
}
