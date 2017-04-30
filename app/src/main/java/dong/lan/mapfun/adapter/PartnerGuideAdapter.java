
package dong.lan.mapfun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.base.BaseItemClickListener;
import dong.lan.mapfun.R;

/**
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
        String sb = "协同用户： " +
                guide.getPartnerInfo();
        holder.users.setText(sb);
        holder.address.setText(guide.getAddress());
    }

    @Override
    public int getItemCount() {
        return guides == null ? 0 : guides.size();
    }

    public void remove(int position) {
        if (position >= 0 && position < getItemCount()) {
            guides.remove(position);
            notifyItemRemoved(position);
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
