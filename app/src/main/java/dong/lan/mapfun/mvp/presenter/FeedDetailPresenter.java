
package dong.lan.mapfun.mvp.presenter;

import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.mapfun.R;
import dong.lan.mapfun.mvp.contract.FeedDetailContract;

/**
 */

public class FeedDetailPresenter implements FeedDetailContract.Presenter {
    private FeedDetailContract.View view;
    private boolean isLike = false;
    private int likeCount = 0;
    private AVOFeed feed = null;


    public FeedDetailPresenter(FeedDetailContract.View view) {
        this.view = view;
    }

    @Override
    public void like(ImageButton likeIcon, TextView likText) {
        if (isLike) {
            likeCount--;
            likeIcon.setImageResource(R.drawable.ic_favorite_border);
        } else {
            likeCount++;
            likeIcon.setImageResource(R.drawable.ic_favorite);
        }
        likText.setText(String.valueOf(likeCount));
        isLike = !isLike;
    }

    @Override
    public void fetchFeed(String feedSeq) {
        if (TextUtils.isEmpty(feedSeq)) {
            view.toast("无效的图趣资源");
        } else {
            try {
                feed = (AVOFeed) AVObject.parseAVObject(feedSeq);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(feed!=null){
                final List<AVOLabel> labels = feed.getLabel();
                view.showContent(feed.getContent());
                view.showLabels(labels);
                //获取图片
                AVQuery<AVOFeedImage> query = new AVQuery<>("FeedImage");
                query.include("image");
                query.whereEqualTo("feed", feed);
                query.findInBackground(new FindCallback<AVOFeedImage>() {
                    @Override
                    public void done(List<AVOFeedImage> list, AVException e) {
                        if (list != null) {
                            view.showFeedImages(list);
                        }
                    }
                });

                //获取收藏量
                AVQuery<AVObject> relationQuery = feed.getLikes().getQuery();
                relationQuery.countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, AVException e) {
                        likeCount = i;
                        view.showFeedLikes(likeCount);
                    }
                });

            }
        }
    }

    @Override
    public void saveLike() {
        if (feed != null && isLike) {
            feed.addLike(AVOUser.getCurrentUser());
        }
    }
}
