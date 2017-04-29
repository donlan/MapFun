
package dong.lan.mapfun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.FriendsAdapter;

/**
 * 好友界面
 */
public class FriendsActivity extends BaseBarActivity implements BaseItemClickListener<AVOUser> {

    private RecyclerView friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        bindView("趣友");

        friendsList = (RecyclerView) findViewById(R.id.friends_list);

        friendsList.setLayoutManager(new GridLayoutManager(this, 1));

        AVOUser avoUser = AVOUser.getCurrentUser();
        AVQuery<AVOUser> query = avoUser.getFriends().getQuery();
        query.include("user");
        query.findInBackground(new FindCallback<AVOUser>() {
            @Override
            public void done(List<AVOUser> list, AVException e) {
                // list 是一个 AVObject 的 List，它包含所有当前 todoFolder 的 tags
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无好友");
                    } else {
                        friendsList.setAdapter(new FriendsAdapter(list, FriendsActivity.this));
                    }
                } else {
                    e.printStackTrace();
                    dialog("获取好友失败，错误码：" + e.getCode());
                }
            }
        });
    }


    @Override
    public void onClick(AVOUser data, int action, int position) {
        Intent intent = new Intent(this, UserCenterActivity.class);
        intent.putExtra("userSeq", data.toString());
        startActivity(intent);
    }
}
