
package dong.lan.mapfun.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.michaldrabik.tapbarmenulib.TapBarMenu;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.base.Config;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.base.ui.customView.PinCircleImageView;
import dong.lan.base.utils.SPHelper;
import dong.lan.library.LabelTextView;
import dong.lan.map.service.LocationService;
import dong.lan.map.utils.MapHelper;
import dong.lan.mapfun.App;
import dong.lan.mapfun.R;
import dong.lan.mapfun.helper.FeedMarkerHelper;
import dong.lan.mapfun.mvp.contract.MainMapContract;
import dong.lan.mapfun.mvp.presenter.MainMapPresenter;
import dong.lan.permission.CallBack;
import dong.lan.permission.Permission;

public class MainActivity extends BaseActivity implements View.OnClickListener, MainMapContract.View, BaiduMap.OnMarkerClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private MapView mapView;
    private TapBarMenu tapBarMenu;
    private ImageView nearUserIv;
    private ImageView nearFeedIv;
    private ImageView addFeedIv;
    private ImageView feedSearchIv;
    private ImageView barLeft;
    private TextView usernameTv;
    private CircleImageView avatar;
    private LabelTextView shareLocationLtv;
    private BaiduMap baiduMap;
    private SlidingRootNav slidingRootNav;
    private boolean isMenuOpen = false;
    private MainMapContract.Presenter presenter;
    private boolean isShare = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        initView();
    }


    private boolean isFirstLoc = true;
    private LocationService.LocationCallback locationCallback = new LocationService.LocationCallback() {
        @Override
        public void onLocation(BDLocation location, String error) {
            if (!TextUtils.isEmpty(error)) {
                dialog(error);
            } else {
                if (isFirstLoc) {
                    MapHelper.setLocation(location, baiduMap, true);
                    isFirstLoc = false;
                    presenter.queryNearFeed();
                }
            }
        }
    };

    private void initView() {
        presenter = new MainMapPresenter(this);

        View menuView = LayoutInflater.from(this)
                .inflate(R.layout.draw_menu, null);

        menuView.findViewById(R.id.me).setOnClickListener(this);
        menuView.findViewById(R.id.favorite).setOnClickListener(this);
        menuView.findViewById(R.id.setting).setOnClickListener(this);
        menuView.findViewById(R.id.partner_guide).setOnClickListener(this);
        menuView.findViewById(R.id.logout).setOnClickListener(this);
        menuView.findViewById(R.id.friends).setOnClickListener(this);
        usernameTv = (TextView) menuView.findViewById(R.id.username);
        avatar = (CircleImageView) menuView.findViewById(R.id.user_avatar);
        avatar.setOnClickListener(this);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuView(menuView)
                .inject();

        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setOnMarkerClickListener(this);
        tapBarMenu = (TapBarMenu) findViewById(R.id.tabBarMenu);
        nearFeedIv = (ImageView) findViewById(R.id.menu_near_feed);
        nearUserIv = (ImageView) findViewById(R.id.menu_near_user);
        addFeedIv = (ImageView) findViewById(R.id.menu_add_feed);
        feedSearchIv = (ImageView) findViewById(R.id.menu_near_search);
        barLeft = (ImageView) findViewById(R.id.bar_left);
        shareLocationLtv = (LabelTextView) findViewById(R.id.shareLocation);
        barLeft.setOnClickListener(this);
        nearUserIv.setOnClickListener(this);
        nearFeedIv.setOnClickListener(this);
        addFeedIv.setOnClickListener(this);
        feedSearchIv.setOnClickListener(this);
        tapBarMenu.setOnClickListener(this);
        shareLocationLtv.setOnClickListener(this);

        List<String> pers = new ArrayList<>(5);
        pers.add(Manifest.permission.ACCESS_FINE_LOCATION);
        pers.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        pers.add(Manifest.permission.READ_PHONE_STATE);
        pers.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        pers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Permission.instance().check(new CallBack<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                if (result == null)
                    App.myApp().getLocationService().registerCallback(MainActivity.this, locationCallback);
            }
        }, this, pers);

        EventBus.getDefault().register(this);

        App.myApp().initIM();

        int r = SPHelper.instance().getInt("radius");
        Config.RADIUS = r == 0 ?10:r;

        AVOUser user = AVOUser.getCurrentUser();
        isShare = user.isShareLocation();
        shareLocationLtv.setText("共享位置：" + (isShare ? "开" : "关"));
        Glide.with(this).load(user.getAvatar() == null ? "" : user.getAvatar().getUrl())
                .error(R.drawable.head)
                .into(avatar);

        usernameTv.setText(user.getDisplayName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLabelQuery(AVOLabel label) {
        if (label != null)
            presenter.queryNearByLabel(Collections.singletonList(label));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
        baiduMap = null;
        presenter.saveUserLocation();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Permission.instance().handleRequestResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.menu_near_feed:
                baiduMap.clear();
                presenter.queryNearFeed();
                tapBarMenu.close();
                break;
            case R.id.menu_add_feed:
                tapBarMenu.close();
                startActivity(new Intent(this, CreateFeedActivity.class));
                break;
            case R.id.menu_near_search:
                startActivity(new Intent(this, FeedQueryActivity.class));
                tapBarMenu.close();
                break;
            case R.id.menu_near_user:
                baiduMap.clear();
                presenter.queryNearUser();
                tapBarMenu.close();
                break;
            case R.id.tabBarMenu:
                tapBarMenu.toggle();
                break;
            case R.id.bar_left:
                if (isMenuOpen)
                    slidingRootNav.closeMenu(true);
                else
                    slidingRootNav.openMenu(true);
                isMenuOpen = !isMenuOpen;
                break;

            case R.id.me:
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
            case R.id.friends:
                startActivity(new Intent(this, FriendsActivity.class));
                break;
            case R.id.favorite:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.partner_guide:
                startActivity(new Intent(this, PartnerGuideActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.logout:
                AVOUser.logOut();
                SPHelper.instance().putInt("radius",10);
                finish();
                break;
            case R.id.user_avatar:
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
            case R.id.shareLocation:
                isShare = !isShare;
                shareLocationLtv.setText("共享位置：" + (isShare ? "开" : "关"));
                presenter.saveShareLocation(isShare);
                break;
        }
    }

    @Override
    public void showNearUser(List<AVOUser> users) {
        if (!users.isEmpty())
            baiduMap.clear();
        for (AVOUser user : users) {
            View view = LayoutInflater.from(this).inflate(R.layout.map_user_head_pin, null);
            PinCircleImageView pin = (PinCircleImageView) view.findViewById(R.id.pin_user_head);
            Glide.with(this)
                    .load(user.getAvatar() == null ? "" : user.getAvatar().getUrl())
                    .error(R.drawable.head)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.head)
                    .into(pin);
            Marker marker = MapHelper.drawMarker(baiduMap,
                    new LatLng(user.getLastLocation().getLatitude(),
                            user.getLastLocation().getLongitude()),
                    BitmapDescriptorFactory.fromView(view), 0.5f, 1f);
            marker.setDraggable(false);
            Bundle data = new Bundle();
            data.putInt("type", 0);
            data.putString("user", user.toString());
            marker.setExtraInfo(data);
        }
    }

    @Override
    public void showNearFeed(List<AVOFeed> feeds) {
        if (!feeds.isEmpty())
            baiduMap.clear();
        for (AVOFeed feed : feeds) {

            Marker marker = MapHelper.drawMarker(baiduMap,
                    new LatLng(feed.getLocation().getLatitude(),
                            feed.getLocation().getLongitude()),
                    BitmapDescriptorFactory.fromView(FeedMarkerHelper.instance()
                            .formMarkerView(this, feed)), 0.5f, 1f);
            marker.setDraggable(false);
            Bundle data = new Bundle();
            data.putInt("type", 1);
            data.putString("feed", feed.toString());
            marker.setExtraInfo(data);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return presenter.handlerMarkerClick(marker);

    }

    @Override
    public Activity activity() {
        return this;
    }
}
