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

package dong.lan.mapfun.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
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
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.customView.TagCloudView;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.FeedDetailImagesAdapter;

public class FeedDetailActivity extends BaseActivity {

    private int likeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        initView();
    }


    private TagCloudView labelTags;
    private RecyclerView feedImagesRv;
    private ImageButton likeIb;
    private TextView content;
    private TextView likeCountTv;
    private AVOFeed feed;
    private boolean isLike = false;
    private List<AVOUser> likes;
    private void initView() {
        labelTags = (TagCloudView) findViewById(R.id.feed_labels_view);
        feedImagesRv = (RecyclerView) findViewById(R.id.feed_images_view);
        likeIb = (ImageButton) findViewById(R.id.like);
        likeCountTv  = (TextView) findViewById(R.id.likes_count);
        content = (TextView) findViewById(R.id.feed_content);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedImagesRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        likeIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike) {
                    likeCount--;
                    likeIb.setImageResource(R.drawable.ic_favorite_border);
                } else {
                    likeCount++;
                    likeIb.setImageResource(R.drawable.ic_favorite);
                }
                likeCountTv.setText(String.valueOf(likeCount));
                isLike = !isLike;
            }
        });

        initData();
    }

    private void initData() {
        String feedStr = getIntent().getStringExtra("feed");
        if (TextUtils.isEmpty(feedStr)) {
            toast("无效的图趣资源");
        } else {
            try {
                feed = (AVOFeed) AVObject.parseAVObject(feedStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(feed!=null){
                content.setText(feed.getContent());
                final List<AVOLabel> labels = feed.getLabel();
                labelTags.setData(labels);
                AVQuery<AVOFeedImage> query = new AVQuery<>("FeedImage");
                query.include("image");
                query.whereEqualTo("feed", feed);
                query.findInBackground(new FindCallback<AVOFeedImage>() {
                    @Override
                    public void done(List<AVOFeedImage> list, AVException e) {
                        if (list != null) {
                            feedImagesRv.setAdapter(new FeedDetailImagesAdapter(list));
                        }
                    }
                });



                AVQuery<AVObject> relationQuery = feed.getLikes().getQuery();
                relationQuery.countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, AVException e) {
                        likeCount = i;
                        likeCountTv.setText(String.valueOf(likeCount));
                    }
                });

            }
        }
    }



    @Override
    public void finish() {
        if (feed != null && isLike) {
            feed.addLike(AVOUser.getCurrentUser(AVOUser.class));
            feed.saveEventually(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    toast(""+e);
                }
            });
        }
        super.finish();
    }
}
