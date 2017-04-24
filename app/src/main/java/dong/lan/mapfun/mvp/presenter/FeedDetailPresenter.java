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

package dong.lan.mapfun.mvp.presenter;

import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.mapfun.R;
import dong.lan.mapfun.mvp.contract.FeedDetailContract;

/**
 * Created by 梁桂栋 on 2017/4/24.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
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
            feed.addLike(AVOUser.getCurrentUser(AVOUser.class));
            feed.saveEventually(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    view.toast(""+e);
                }
            });
        }
    }
}
