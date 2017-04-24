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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.customView.TagCloudView;
import dong.lan.base.utils.FileUtil;
import dong.lan.map.activity.PickLocationActivity;
import dong.lan.map.service.Config;
import dong.lan.map.service.LocationService;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.FeedImagesAdapter;
import dong.lan.mapfun.event.PickLabelEvent;

/**
 * 发布内容界面
 */
public class CreateFeedActivity extends BaseActivity implements TagCloudView.OnTagClickListener, View.OnClickListener {


    private static final String TAG = CreateFeedActivity.class.getSimpleName();
    private TextView locText;
    private TagCloudView tagCloudView;
    private RecyclerView photoViews;
    private EditText feedInput;
    private ImageButton visitableIb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_feed);

        initView();
    }

    private void initView() {
        locText = (TextView) findViewById(R.id.publish_feed_loc_text);
        tagCloudView = (TagCloudView) findViewById(R.id.label_tags);
        photoViews = (RecyclerView) findViewById(R.id.feed_images_list);
        feedInput = (EditText) findViewById(R.id.new_feed_content);
        tagCloudView.setOnTagClickListener(this);
        photoViews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        findViewById(R.id.publish_feed_pick_image).setOnClickListener(this);
        findViewById(R.id.publish_feed_pick_loc).setOnClickListener(this);
        findViewById(R.id.publish_feed).setOnClickListener(this);
        findViewById(R.id.publish_feed_label).setOnClickListener(this);
        visitableIb = (ImageButton) findViewById(R.id.publish_feed_visitable);
        visitableIb.setOnClickListener(this);


        FunctionConfig config = new FunctionConfig();
        config.setCompress(true);
        config.setEnableCrop(true);
        config.setMaxSelectNum(3);
        config.setSelectMode(FunctionConfig.MODE_MULTIPLE);
        config.setCopyMode(FunctionConfig.CROP_MODEL_3_4);
        config.setType(LocalMediaLoader.TYPE_IMAGE);
        PictureConfig.init(config);


        EventBus.getDefault().register(this);

        //默认使用最近一次定位成功的位置信息
        BDLocation location = LocationService.service().getLastLocation();
        if (location != null) {
            locText.setVisibility(View.VISIBLE);
            locText.setText(location.getAddrStr());
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }


    private List<AVOLabel> labels;

    /**
     * 将PickLabelActivity返回的标签显示在界面中
     *
     * @param labelEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    void onPickLabel(PickLabelEvent labelEvent) {
        labels = labelEvent.labels;
        tagCloudView.setData(labels);
        tagCloudView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTagClick(int postion) {

    }

    /**
     * 选择图片
     */
    private List<String> paths;
    private FeedImagesAdapter feedImagesAdapter;
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> list) {
            if (list != null) {
                if (paths == null)
                    paths = new ArrayList<>();
                for (LocalMedia media : list) {
                    paths.add(media.getCompressPath());
                }
                if (feedImagesAdapter == null) {
                    photoViews.setVisibility(View.VISIBLE);
                    feedImagesAdapter = new FeedImagesAdapter(paths);
                    photoViews.setAdapter(feedImagesAdapter);
                } else {
                    feedImagesAdapter.reset(paths);
                }
            }
        }
    };


    private boolean isPublicFeed = false; //默认发布仅自己可见的内容

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.publish_feed:
                publish();
                break;
            //选择标签
            case R.id.publish_feed_label:
                startActivity(new Intent(this, PickLabelActivity.class));
                break;
            //选择图片
            case R.id.publish_feed_pick_image:
                PictureConfig.getPictureConfig().openPhoto(this, resultCallback);
                break;
            //选择地理坐标
            case R.id.publish_feed_pick_loc:
                startActivityForResult(new Intent(this, PickLocationActivity.class), Config.RESULT_LOCATION);
                break;
            //内容是否只有自己可见
            case R.id.publish_feed_visitable:
                if (isPublicFeed) {
                    visitableIb.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    visitableIb.setImageResource(R.drawable.ic_eye_checked);
                }
                isPublicFeed = !isPublicFeed;
                break;
        }
    }

    /**
     * 发布内容
     */
    private void publish() {
        if (longitude == 0 || latitude == 0) {
            dialog("没有获取到位置信息");
            return;
        }
        String content = feedInput.getText().toString();
        if (TextUtils.isEmpty(content) || content.length() < 10) {
            dialog("内容太少啦");
            return;
        }
        if (labels == null || labels.size() < 1) {
            dialog("至少需要一个标签");
            return;
        }
        alert("发布中...");

        final AVOFeed avoFeed = new AVOFeed();
        avoFeed.setCreator(AVOUser.getCurrentUser());
        avoFeed.setLocation(latitude, longitude);
        avoFeed.setContent(content);
        avoFeed.setLabel(labels);
        avoFeed.setPublic(isPublicFeed);
        avoFeed.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                dismiss();
                if (e == null) {
                    if (paths != null) {
                        for (String p : paths) {
                            try {
                                final AVFile file = new AVFile(FileUtil.PathToFileName(p),FileUtil.File2byte(p));
                                file.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if(e == null) {
                                            AVOFeedImage avoFeedImage = new AVOFeedImage();
                                            avoFeedImage.setCreator(AVOUser.getCurrentUser());
                                            avoFeedImage.setImage(file);
                                            avoFeedImage.setFeed(avoFeed);
                                            avoFeedImage.saveEventually();
                                        }else{
                                            alert("上传图片失败");
                                        }
                                    }
                                }, new ProgressCallback() {
                                    @Override
                                    public void done(Integer integer) {
                                        alert("图片上传中："+integer);
                                        if(integer>=100)
                                            dismiss();
                                    }
                                });
                            }catch (Exception ep){
                                Log.d(TAG, "done: "+ep.getMessage());
                            }
                        }
                    }else {
                        dismiss();
                    }
                } else {
                    dismiss();
                    e.printStackTrace();
                    dialog("发布失败，错误码：" + e.getCode());
                }
            }
        });
    }


    private double latitude;
    private double longitude;
    private String address;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回的地理位置信息
        if (requestCode == Config.RESULT_LOCATION && resultCode == Config.RESULT_LOCATION) {
            address = data.getStringExtra(Config.LOC_ADDRESS);
            latitude = data.getDoubleExtra(Config.LATITUDE, 0);
            longitude = data.getDoubleExtra(Config.LONGITUDE, 0);
            locText.setVisibility(View.VISIBLE);
            locText.setText(address);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
