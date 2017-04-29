
package dong.lan.mapfun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.FavoriteFeedsAdapter;

public class FavoriteActivity extends BaseBarActivity implements BaseItemClickListener<AVOFeed> {


    private RecyclerView favoriteFeedList;
    private AVOUser user;
    private FavoriteFeedsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        bindView("收藏");

        favoriteFeedList = (RecyclerView) findViewById(R.id.favorite_feed_list);

        favoriteFeedList.setLayoutManager(new GridLayoutManager(this, 1));

        init();

    }

    private void init() {
        user = AVOUser.getCurrentUser();

        AVQuery<AVOFeed> query = new AVQuery<>("Feed");
        query.include("labels");
        query.whereEqualTo("likes", user);
        query.findInBackground(new FindCallback<AVOFeed>() {
            @Override
            public void done(List<AVOFeed> list, AVException e) {
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无收藏");
                    } else {
                        adapter = new FavoriteFeedsAdapter(list, FavoriteActivity.this);
                        favoriteFeedList.setAdapter(adapter);
                    }
                } else {
                    dialog("获取收藏失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void onClick(AVOFeed data, int action, int position) {
        if (action == 1) {
            data.removeLike(user);
            adapter.remove(position);
        } else if (action == 0) {
            Intent intent = new Intent(this, FeedDetailActivity.class);
            intent.putExtra("feed", data.toString());
            startActivity(intent);
        }
    }
}
