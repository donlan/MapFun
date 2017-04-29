
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
 * 收藏内容适配器
 */

public class FavoriteFeedsAdapter extends RecyclerView.Adapter<FavoriteFeedsAdapter.ViewHolder> {


    private List<AVOFeed> feeds;
    private BaseItemClickListener<AVOFeed> clickListener;

    public FavoriteFeedsAdapter(List<AVOFeed> feeds, BaseItemClickListener<AVOFeed> itemClickListener) {
        this.feeds = feeds;
        clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_feed, null);
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

    public void remove(int position) {
        feeds.remove(position);
        notifyItemRemoved(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LabelTextView labels;
        ImageView image;
        TextView content;
        LabelTextView unFavorite;

        public ViewHolder(View itemView) {
            super(itemView);

            labels = (LabelTextView) itemView.findViewById(R.id.item_feed_label);
            image = (ImageView) itemView.findViewById(R.id.item_feed_image);
            content = (TextView) itemView.findViewById(R.id.item_feed_content);

            unFavorite = (LabelTextView) itemView.findViewById(R.id.unFavorite);

            unFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition();
                    if (clickListener != null)
                        clickListener.onClick(feeds.get(p), 1, p);
                }
            });

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
