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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dong.lan.mapfun.R;

/**
 * Created by 梁桂栋 on 2017/4/15.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class FeedImagesAdapter extends RecyclerView.Adapter<FeedImagesAdapter.ViewHolder> {


    private List<String> imagesPath;

    public FeedImagesAdapter(List<String> imagesPath) {
        this.imagesPath = imagesPath;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_image_hint, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(imagesPath.get(position))
                .into(holder.feedImage);
    }

    @Override
    public int getItemCount() {
        return imagesPath == null ? 0 : imagesPath.size();
    }

    public void reset(List<String> paths) {
        if (imagesPath == null)
            imagesPath = new ArrayList<>();
        imagesPath.clear();
        imagesPath.addAll(paths);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView feedImage;
        ImageButton del;

        public ViewHolder(View itemView) {
            super(itemView);
            feedImage = (ImageView) itemView.findViewById(R.id.item_feed_hint_image);
            del = (ImageButton) itemView.findViewById(R.id.item_feed_image_del);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagesPath.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                }
            });
        }
    }
}
