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

import java.util.List;

import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.mapfun.R;

/**
 * Created by 梁桂栋 on 2017/4/16.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 * 协同导航列表适配器
 */

public class PartnerGuideAdapter extends RecyclerView.Adapter<PartnerGuideAdapter.ViewHolder> {


    private List<AVOGuide> guides;
    private BaseItemClickListener<AVOGuide> clickListener;

    public PartnerGuideAdapter(List<AVOGuide> guides, BaseItemClickListener<AVOGuide> itemClickListener) {
        this.guides = guides;
        clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partner_guide, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVOGuide guide = guides.get(position);
        StringBuilder sb = new StringBuilder();
        sb.append("协同用户： ");
        for (AVOUser label : guide.getPartner()) {
            sb.append("#");
            sb.append(label.getUsername());
            sb.append("  ");
        }
        holder.users.setText(sb.toString());
        holder.address.setText(guide.getAddress());
    }

    @Override
    public int getItemCount() {
        return guides == null ? 0 : guides.size();
    }

    public void remove(int resultCode) {
        if (resultCode >= 0 && resultCode < getItemCount()) {
            guides.remove(resultCode);
            notifyItemRemoved(resultCode);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        TextView users;

        public ViewHolder(View itemView) {
            super(itemView);

            address = (TextView) itemView.findViewById(R.id.item_partner_address);
            users = (TextView) itemView.findViewById(R.id.item_partner_users);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    if (clickListener != null)
                        clickListener.onClick(guides.get(p), 0, p);
                }
            });

        }
    }
}
