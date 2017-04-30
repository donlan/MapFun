
package dong.lan.mapfun.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.clusterutil.ui.RouteLineAdapter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.blankj.ALog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.customView.MapPinNumView;
import dong.lan.library.LabelTextView;
import dong.lan.map.service.LocationService;
import dong.lan.map.utils.MapHelper;
import dong.lan.mapfun.App;
import dong.lan.mapfun.R;
import dong.lan.mapfun.uitls.StringUtils;

import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;
import static dong.lan.base.ui.Dialog.CLICK_RIGHT;

/**
 * 实时显示相互位置界面
 */
public class GuidingActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener {


    private static final String APP_FOLDER_NAME = "MapFun";
    public static final String ROUTE_PLAN_NODE = "plan_node";

    private MapView mapView;

    private LabelTextView finishGuiding;

    private BaiduMap baiduMap;

    private Marker destinationPin; //目的地在地图上的图标

    private Marker mePin; //自己的在地图上的图标

    private Marker otherPin; //对方字地图上的图标

    private AVOUser me;

    private AVOUser other;

    private RoutePlanSearch planSearch = RoutePlanSearch.newInstance();
    private WalkingRouteResult nowResultwalk;
    private boolean hasShownDialogue;
    private boolean useDefaultIcon;

    private int position = -1;
    private String mSDCardPath;
    private String authinfo;
    private boolean hasInitSuccess;
    private BNRoutePlanNode.CoordinateType coType = BNRoutePlanNode.CoordinateType.BD09LL;
    private dong.lan.base.ui.Dialog dialog;
    private int planCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guiding);

        mapView = (MapView) findViewById(R.id.mapView);
        finishGuiding = (LabelTextView) findViewById(R.id.finish_guiding);

        baiduMap = mapView.getMap();

        finishGuiding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishGuiding();
            }
        });

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker == destinationPin) {
                    NaviParaOption para = new NaviParaOption()
                            .startPoint(mePin.getPosition()).endPoint(destinationPin.getPosition());
                    try {
                        BaiduMapNavigation.openBaiduMapNavi(para, GuidingActivity.this);
                    } catch (BaiduMapAppNotSupportNaviException e) {
                        e.printStackTrace();
                        if (dialog == null)
                            dialog = new dong.lan.base.ui.Dialog(GuidingActivity.this);

                        dialog.setMessageText("您尚未安装百度地图app或app版本过低，点击确认安装？")
                                .setRightText("安装")
                                .setClickListener(new dong.lan.base.ui.Dialog.DialogClickListener() {
                                    @Override
                                    public boolean onDialogClick(int which) {
                                        if (which == CLICK_RIGHT) {
                                            OpenClientUtil.getLatestBaiduMapApp(GuidingActivity.this);
                                        }
                                        return true;
                                    }
                                }).show();
                    }
                }
                return true;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(App.myApp())) {
                run();
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + App.myApp().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            run();
        }
    }

    private void run() {
        init();
        if (initDirs())
            initNav();
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initNav() {

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {

                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                toast(authinfo);
            }

            public void initSuccess() {
                toast("百度导航引擎初始化成功");
                hasInitSuccess = true;
                initSetting();
                planCount++;
            }

            public void initStart() {
                toast("百度导航引擎初始化开始");
            }

            public void initFailed() {
                toast("百度导航引擎初始化失败");
            }

        }, null, ttsHandler, ttsPlayStateListener);
    }

    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String[] authComArr = {Manifest.permission.READ_PHONE_STATE};


    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9354030");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    // showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };


    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };


    /**
     * 结束导航，同时删除该次协同导航记录
     */
    private void finishGuiding() {
        if (guide != null) {
            //结束并退出临时会话
            if (locConv != null) {
                locConv.kickMembers(Collections.singletonList(other.getObjectId()), new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        locConv.quit(new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {

                            }
                        });
                    }
                });

            }
            guide.deleteEventually();
            position = getIntent().getIntExtra("position", -1);
            finish();
        }
    }

    @Override
    public void finish() {
        setResult(position);
        super.finish();
    }

    private AVOGuide guide;

    private void init() {
        //跳转时候携带的导航序列信息
        final String guideStr = getIntent().getStringExtra("guide");
        if (TextUtils.isEmpty(guideStr)) {
            toast("无效协同导航参数");
            finish();
        } else {
            //反序列化导航信息
            try {
                guide = (AVOGuide) AVObject.parseAVObject(guideStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (guide == null) {
                toast("无效协同导航参数");
                finish();
                return;
            }
            baiduMap.setOnMarkerClickListener(this);

            //获取该导航的最新信息
            AVQuery<AVOGuide> query = new AVQuery<>("Guide");
            query.include("partner");
            query.getInBackground(guide.getObjectId(), new GetCallback<AVOGuide>() {
                @Override
                public void done(AVOGuide avoGuide, AVException e) {
                    if (e == null) {
                        if (avoGuide == null) {
                            toast("无此协同导航数据");
                        } else {
                            guide = avoGuide;
                            prepare(guide);
                        }
                    } else {
                        dialog("获取协同导航数据失败，错误码：" + e.getCode());
                    }
                }
            });
        }

    }


    private LocationService.LocationCallback locationCallback = new LocationService.LocationCallback() {
        @Override
        public void onLocation(BDLocation location, String error) {
            if (TextUtils.isEmpty(error)) {
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                MapHelper.setLocation(point, baiduMap, mePin == null);
                if (mePin == null) {
                    MapPinNumView dstPinView = new MapPinNumView(GuidingActivity.this, "自己", 0xff2ecc71, 14, 0xffffffff);
                    mePin = MapHelper.drawMarker(baiduMap, new LatLng(guide.getLocation().getLatitude(),
                                    guide.getLocation().getLongitude()),
                            BitmapDescriptorFactory.fromView(dstPinView));
                    mePin.setDraggable(false);
                } else {
                    mePin.setPosition(point);

                    //每次定位成功，发送一次位置信息
                    if (locConv != null) {
                        AVIMLocationMessage message = new AVIMLocationMessage();
                        message.setText(StringUtils.GuidingText(location));
                        message.setLocation(new AVGeoPoint(point.latitude, point.longitude));
                        locConv.sendMessage(message, new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                if (e != null)
                                    e.printStackTrace();
                            }
                        });
                    }
                }
                if(planCount<10) {
                    planCount++;
                    if (planCount == 4) {
                        setMyPlaneRoute();
                    }
                }
            } else {
                toast(error);
            }
        }
    };

    private void setMyPlaneRoute() {

        if (mePin == null) {
            return;
        }


        BNRoutePlanNode sNode = new BNRoutePlanNode(mePin.getPosition().longitude, mePin.getPosition().latitude, "起点", null, coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(destinationPin.getPosition().longitude, destinationPin.getPosition().latitude, "终点", null, coType);

        List<BNRoutePlanNode> list = new ArrayList<>(2);
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));

        PlanNode sPlanNode = PlanNode.withLocation(mePin.getPosition());
        PlanNode ePlanNode = PlanNode.withLocation(destinationPin.getPosition());

        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        drivingRoutePlanOption.from(sPlanNode);
        drivingRoutePlanOption.to(ePlanNode);
        WalkingRoutePlanOption option = new WalkingRoutePlanOption();
        option.from(sPlanNode);
        option.to(ePlanNode);
        BikingRoutePlanOption bikingRoutePlanOption = new BikingRoutePlanOption();
        bikingRoutePlanOption.from(sPlanNode);
        bikingRoutePlanOption.to(ePlanNode);

        planSearch.bikingSearch(bikingRoutePlanOption);
        planSearch.walkingSearch(option);
        planSearch.drivingSearch(drivingRoutePlanOption);

    }

    private void setOtherPlaneRoute() {
        PlanNode sPlanNode = PlanNode.withLocation(otherPin.getPosition());
        PlanNode ePlanNode = PlanNode.withLocation(destinationPin.getPosition());

        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        drivingRoutePlanOption.from(sPlanNode);
        drivingRoutePlanOption.to(ePlanNode);
        WalkingRoutePlanOption option = new WalkingRoutePlanOption();
        option.from(sPlanNode);
        option.to(ePlanNode);
        BikingRoutePlanOption bikingRoutePlanOption = new BikingRoutePlanOption();
        bikingRoutePlanOption.from(sPlanNode);
        bikingRoutePlanOption.to(ePlanNode);

        planSearch.bikingSearch(bikingRoutePlanOption);
        planSearch.walkingSearch(option);
        planSearch.drivingSearch(drivingRoutePlanOption);

    }


    private void prepare(AVOGuide guide) {

        if (AVOUser.getCurrentUser().getObjectId().equals(guide.getCreator().getObjectId())) {
            finishGuiding.setVisibility(View.VISIBLE);
        }


        List<AVOUser> userList = guide.getPartner();
        me = AVOUser.getCurrentUser();
        if (userList.get(0).getObjectId().equals(me.getObjectId())) {
            other = userList.get(1);
        } else {
            other = userList.get(0);
        }


        planSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && !result.getRouteLines().isEmpty()) {
                    result.getRouteLines().get(0);
                    WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    toast("无步行路线");
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                ALog.d(transitRouteResult);
            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
                ALog.d(massTransitRouteResult);
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && !result.getRouteLines().isEmpty()) {
                    result.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    toast("无驾车路线");
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
                ALog.d(indoorRouteResult);
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult result) {
                if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && !result.getRouteLines().isEmpty()) {
                    result.getRouteLines().get(0);
                    BikingRouteOverlay overlay = new BikingRouteOverlay(baiduMap);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                } else {
                    toast("无骑行路线");
                }
            }
        });


        //绘制目的地的marker
        MapPinNumView dstPinView = new MapPinNumView(this, "目的地", 0xffe67e22, 14, 0xffffffff);
        destinationPin = MapHelper.drawMarker(baiduMap, new LatLng(guide.getLocation().getLatitude(),
                        guide.getLocation().getLongitude()),
                BitmapDescriptorFactory.fromView(dstPinView));
        destinationPin.setDraggable(false);


        //创建临时会话，并通过该会话发送发送位置信息

        App.myApp().getAvimClient().getQuery()
                .whereEqualTo("objectId", guide.getConvId())
                .findInBackground(new AVIMConversationQueryCallback() {
                    @Override
                    public void done(List<AVIMConversation> list, AVIMException e) {
                        if (list == null || list.isEmpty()) {
                            toast("无效会话通道");
                        } else {
                            if (e == null) {
                                locConv = list.get(0);
                            } else {
                                toast("创建通信信道失败");
                            }
                        }
                    }
                });
    }


    private AVIMConversation locConv;


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    private MyMessageHandler myMessageHandler;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (myMessageHandler == null)
            myMessageHandler = new MyMessageHandler();
        LocationService.service().registerCallback(this,locationCallback);
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, myMessageHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        LocationService.service().unregisterCallback(this);
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, myMessageHandler);
    }


    private class MyMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client) {
            if (locConv != null && avimConversation.getConversationId().equals(locConv.getConversationId())) {
                if (message instanceof AVIMLocationMessage) {
                    AVIMLocationMessage locMsg = (AVIMLocationMessage) message;
                    AVGeoPoint point = locMsg.getLocation();

                    if (otherPin == null) {
                        //绘制对方的marker
                        MapPinNumView otherView = new MapPinNumView(GuidingActivity.this, "对方", 0xffe67e22, 14, 0xffffffff);
                        otherPin = MapHelper.drawMarker(baiduMap, new LatLng(other.getLastLocation().getLatitude()
                                        , other.getLastLocation().getLatitude()),
                                BitmapDescriptorFactory.fromView(otherView));
                        otherPin.setDraggable(false);
                        setOtherPlaneRoute();
                    }
                    otherPin.setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
                }
            }
            super.onMessage(message, avimConversation, client);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mapView != null) {
            mapView.onDestroy();
        }
        if (baiduMap != null) {
            baiduMap.clear();
            baiduMap.removeMarkerClickListener(this);
        }
        baiduMap = null;
        mapView = null;

        planSearch.destroy();


        if (LocationService.service().getLastLocation() != null && me != null) {
            me.setLastLocation(LocationService.service().getLastLocation().getLatitude(),
                    LocationService.service().getLastLocation().getLongitude());
            me.saveEventually();
        }
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    private class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    dismiss();
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }


    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.location);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
            }
            return null;
        }
    }


    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

//            for (Activity ac : activityList) {
//
//                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
//
//                    return;
//                }
//            }

            //如果需要定位成功就跳转到导航界面，请取消一下5行注释
//            Intent intent = new Intent(GuidingActivity.this, BNDemoGuideActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(ROUTE_PLAN_NODE, mBNRoutePlanNode);
//            intent.putExtras(bundle);
//            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            toast("算路失败");
        }
    }

    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    toast("缺少导航基本的权限!");
                    return;
                }
            }
            initNav();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }

        }

    }
}
