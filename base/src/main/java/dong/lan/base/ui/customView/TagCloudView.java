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

package dong.lan.base.ui.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dong.lan.base.R;
import dong.lan.library.LabelTextView;


/**
 * Created by 梁桂栋 on 17-1-16 ： 下午3:30.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public class TagCloudView extends RecyclerView {


    public TagCloudView(Context context) {
        this(context, null);
    }

    public TagCloudView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagCloudView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new TagCloudLayoutManager());
    }


    public void setData(List<? extends ItemLabelData> data) {
        this.data = data;
        if (getAdapter() == null)
            setAdapter(new Adapter());
        else
            getAdapter().notifyDataSetChanged();
    }


    private List<? extends ItemLabelData> data;


    public ItemLabelData getData(int position) {
        return data.get(position);
    }

    private class Adapter extends RecyclerView.Adapter<TagViewHolder> {


        @Override
        public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, null);
            final TagViewHolder holder = new TagViewHolder(view);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTagClick(holder.getLayoutPosition());
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(TagViewHolder holder, int position) {
            holder.labelTextView.setText(data.get(position).labelText());
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }
    }


    private static class TagViewHolder extends ViewHolder {

        LabelTextView labelTextView;

        public TagViewHolder(View itemView) {
            super(itemView);
            labelTextView = (LabelTextView) itemView.findViewById(R.id.ltv);
        }
    }


    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }


    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(int postion);
    }


}
