
package dong.lan.mapfun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.base.Config;
import dong.lan.base.ui.customView.CircleImageButton;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.base.utils.FileUtil;
import dong.lan.mapfun.R;
import dong.lan.mapfun.adapter.FeedsAdapter;


/**
 * 用户中心界面
 */
public class UserCenterActivity extends BaseActivity implements BaseItemClickListener<AVOFeed> {


    private CircleImageView avatar;
    private TextView feedsStatusTv;
    private TextView userFollowerTv;
    private RecyclerView feedListView;
    private CircleImageButton userActionIb;
    private TextView usernameTv;
    private String userSeq;
    private AVOUser user;
    private int follower = 0;
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> list) {
            if (list != null && !list.isEmpty()) {
                LocalMedia media = list.get(0);
                String path = media.getCompressPath();

                Glide.with(UserCenterActivity.this).load(path)
                        .error(R.drawable.head)
                        .into(avatar);

                if (user.getAvatar() != null) {
                    user.getAvatar().deleteEventually();
                }
                AVFile file = new AVFile(FileUtil.PathToFileName(path), FileUtil.File2byte(path));
                file.saveInBackground();
                user.setAvatar(file);
                user.saveEventually();
            }
        }
    };
    private String objId;
    private boolean notMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        avatar = (CircleImageView) findViewById(R.id.user_avatar);
        feedsStatusTv = (TextView) findViewById(R.id.user_feeds);
        userFollowerTv = (TextView) findViewById(R.id.user_focus);
        feedListView = (RecyclerView) findViewById(R.id.user_fees_list);
        userActionIb = (CircleImageButton) findViewById(R.id.user_action);
        usernameTv = (TextView) findViewById(R.id.username);
        feedListView.setLayoutManager(new GridLayoutManager(this, 1));
        userActionIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toChat();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureConfig.getPictureConfig().openPhoto(UserCenterActivity.this, resultCallback);
            }
        });
        userFollowerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFollower();
            }
        });
        userSeq = getIntent().getStringExtra("userSeq");

        objId = getIntent().getStringExtra("id");

        init();

    }

    private void addFollower() {
        if (AVOUser.getCurrentUser().getObjectId().equals(user.getObjectId())) {
            //点击自己的关注者
            startActivity(new Intent(this, FriendsActivity.class));
        } else {
            //关注其他用户
            user.addFriend(AVOUser.getCurrentUser());
            userFollowerTv.setText((follower + 1) + " 个关注者");
        }
    }

    //前往聊天
    private void toChat() {
        if (AVOUser.getCurrentUser().getObjectId().equals(user.getObjectId())) {
            return;
        }
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra(Config.INTENT_USER, user.toString());
        startActivity(chatIntent);
    }

    private void init() {

        if (TextUtils.isEmpty(userSeq) && TextUtils.isEmpty(objId)) {
            userActionIb.setVisibility(View.GONE);
            user = AVOUser.getCurrentUser();
            setUpView(user);
        } else if (!TextUtils.isEmpty(userSeq)) {
            try {
                user = (AVOUser) AVObject.parseAVObject(userSeq);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (user == null) {
                toast("非法用户数据");
                finish();
                return;
            }
            setUpView(user);
        } else if (!TextUtils.isEmpty(objId)) {
            AVQuery<AVOUser> query = new AVQuery<>("MyUser");
            query.getInBackground(objId, new GetCallback<AVOUser>() {
                @Override
                public void done(AVOUser avUser, AVException e) {
                    if (e == null) {
                        if (avUser == null) {
                            toast("无此用户");
                        } else {
                            user = avUser;
                            setUpView(user);
                        }
                    } else {
                        dialog("获取用户信息失败，错误码：" + e.getCode());
                    }
                }
            });
        }
        AVQuery<AVObject> relationQuery = user.getFriends().getQuery();
        relationQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                follower = i;
                userFollowerTv.setText(follower + " 个关注者");
            }
        });

        FunctionConfig config = new FunctionConfig();
        config.setCompress(true);
        config.setEnableCrop(true);
        config.setMaxSelectNum(1);
        config.setSelectMode(FunctionConfig.MODE_MULTIPLE);
        config.setCopyMode(FunctionConfig.CROP_MODEL_1_1);
        config.setType(LocalMediaLoader.TYPE_IMAGE);
        PictureConfig.init(config);
    }


    private FeedsAdapter feedsAdapter;

    private void setUpView(AVOUser user) {
        Glide.with(this).load(user.getAvatar() == null ? "" : user.getAvatar().getUrl())
                .error(R.drawable.head)
                .into(avatar);
        usernameTv.setText(user.getDisplayName());
        notMe = !user.getObjectId().equals(AVOUser.getCurrentUser().getObjectId());

        AVQuery<AVOFeed> query = new AVQuery<>("Feed");
        query.whereEqualTo("creator", user);
        query.include("labels");

        if (notMe) {
            query.whereEqualTo("isPublic", true);
        }
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVOFeed>() {
            @Override
            public void done(List<AVOFeed> list, AVException e) {
                feedsStatusTv.setText((list == null ? 0 : list.size()) + "个图趣");
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无图趣");
                    } else {
                        feedsAdapter = new FeedsAdapter(list, UserCenterActivity.this);
                        feedListView.setAdapter(feedsAdapter);
                    }
                } else {
                    dialog("获取用户图趣失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void onClick(AVOFeed data, int action, int position) {
        Intent feedIntent = new Intent(this, FeedDetailActivity.class);
        feedIntent.putExtra("feed", data.toString());
        startActivity(feedIntent);
    }
}
