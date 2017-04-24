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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.base.ui.customView.TagCloudView;
import dong.lan.library.LabelTextView;
import dong.lan.mapfun.R;

/**
 * 内容搜索界面
 */
public class FeedQueryActivity extends BaseBarActivity implements TagCloudView.OnTagClickListener {


    private TagCloudView labelTags;
    private LabelTextView selectLabelLtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_query);
        bindView("选择搜索标签");
        labelTags = (TagCloudView) findViewById(R.id.query_label_tags);

        labelTags.setOnTagClickListener(this);
        selectLabelLtv = (LabelTextView) findViewById(R.id.select_label);

        AVQuery<AVOLabel> query = new AVQuery<>("Label");
        query.orderByDescending("-updatedAt");
        query.limit(100);
        query.findInBackground(new FindCallback<AVOLabel>() {
            @Override
            public void done(List<AVOLabel> list, AVException e) {
                if (e == null) {
                    if (list != null) {
                        labelTags.setData(list);
                    }
                } else {
                    dialog("获取标签失败，错误码：" + e.getCode());
                }
            }
        });
    }


    @Override
    public void finish() {
        if (label != null)
            EventBus.getDefault().post(label);
        super.finish();
    }

    private AVOLabel label;

    @Override
    public void onTagClick(int postion) {
        label = (AVOLabel) labelTags.getData(postion);
        selectLabelLtv.setText(label.labelText());
    }
}
