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

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.customView.TagCloudView;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.FeedDetailImagesAdapter;
import dong.lan.mapfun.mvp.contract.FeedDetailContract;
import dong.lan.mapfun.mvp.presenter.FeedDetailPresenter;

/**
 * 内容详情界面
 */

public class FeedDetailActivity extends BaseActivity implements FeedDetailContract.View{


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
    private FeedDetailContract.Presenter presenter;

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
                presenter.like(likeIb,likeCountTv);
            }
        });

        presenter = new FeedDetailPresenter(this);

        presenter.fetchFeed(getIntent().getStringExtra("feed"));
    }


    @Override
    public void finish() {
        presenter.saveLike();
        super.finish();
    }

    @Override
    public Activity activity() {
        return this;
    }

    @Override
    public void showFeedImages(List<AVOFeedImage> images) {
        feedImagesRv.setAdapter(new FeedDetailImagesAdapter(images));
    }

    @Override
    public void showFeedLikes(int likeCount) {
        likeCountTv.setText(likeCount+" 人喜欢");
    }

    @Override
    public void showLabels(List<AVOLabel> labels) {
        labelTags.setData(labels);
    }

    @Override
    public void showContent(String content) {
        this.content.setText(content);
    }
}
