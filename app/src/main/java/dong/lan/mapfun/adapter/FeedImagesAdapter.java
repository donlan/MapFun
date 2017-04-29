
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
 * 内容发布时的图片显示适配器
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
