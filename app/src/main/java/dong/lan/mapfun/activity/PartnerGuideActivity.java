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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.Collections;
import java.util.List;

import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.base.ui.base.Config;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.PartnerGuideAdapter;

/**
 * 所有发起协同导航的列表页面
 */
public class PartnerGuideActivity extends BaseBarActivity implements BaseItemClickListener<AVOGuide> {

    private RecyclerView partnerGuideList;

    private AVOUser user;
    private PartnerGuideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_guide);

        bindView("协同导航");

        partnerGuideList = (RecyclerView) findViewById(R.id.partner_guide_list);

        partnerGuideList.setLayoutManager(new GridLayoutManager(this, 1));

        user = AVOUser.getCurrentUser(AVOUser.class);


        init();

    }

    private void init() {
        AVQuery<AVOGuide> query = new AVQuery<>("Guide");
        query.include("partner");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereNotEqualTo("status", Config.GUIDE_STATUS_FINISH);
        query.whereContainedIn("partner", Collections.singleton(user));
        query.findInBackground(new FindCallback<AVOGuide>() {
            @Override
            public void done(List<AVOGuide> list, AVException e) {
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无数据");
                    } else {
                        adapter = new PartnerGuideAdapter(list, PartnerGuideActivity.this);
                        partnerGuideList.setAdapter(adapter);
                    }
                } else {
                    dialog("获取协同导航数据失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void onClick(AVOGuide data, int action, int position) {
        if (action == 0) {
            Intent intent = new Intent(this, GuidingActivity.class);
            intent.putExtra("guide", data.toString());
            intent.putExtra("position",position);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode ==1 && resultCode>=0){
            adapter.remove(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
