
package dong.lan.mapfun.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.blankj.ALog;

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

/**
 * 实时显示相互位置界面
 */
public class GuidingActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener {

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

    private int position=-1;

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

        init();
    }


    /**
     * 结束导航，同时删除该次协同导航记录
     */
    private void finishGuiding() {
        if(guide!=null){
            //结束并退出临时会话
            if(locConv!=null){
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
            position = getIntent().getIntExtra("position",-1);
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
        if(TextUtils.isEmpty(guideStr)){
            toast("无效协同导航参数");
            finish();
        }else{
            //反序列化导航信息
            try {
                guide  = (AVOGuide) AVObject.parseAVObject(guideStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(guide == null){
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
                    if(e == null){
                        if(avoGuide == null){
                            toast("无此协同导航数据");
                        }else {
                            guide = avoGuide;
                            prepare(guide);
                        }
                    }else{
                        dialog("获取协同导航数据失败，错误码："+e.getCode());
                    }
                }
            });
        }

    }


    private LocationService.LocationCallback locationCallback = new LocationService.LocationCallback() {
        @Override
        public void onLocation(BDLocation location, String error) {
            if(TextUtils.isEmpty(error)){
                LatLng point  = new LatLng(location.getLatitude(),location.getLongitude());
                MapHelper.setLocation(point,baiduMap,mePin == null);
                if(mePin == null){
                    MapPinNumView dstPinView = new MapPinNumView(GuidingActivity.this,"自己",0xff2ecc71,14,0xffffffff);
                    mePin =  MapHelper.drawMarker(baiduMap,new LatLng(guide.getLocation().getLatitude(),
                                    guide.getLocation().getLongitude()),
                            BitmapDescriptorFactory.fromView(dstPinView));
                    mePin.setDraggable(false);
                    setMyPlaneRoute();
                }else{
                    mePin.setPosition(point);

                    //每次定位成功，发送一次位置信息
                    if(locConv!=null){
                        AVIMLocationMessage message = new AVIMLocationMessage();
                        message.setText(me.getObjectId());
                        message.setLocation(new AVGeoPoint(point.latitude,point.longitude));
                        locConv.sendMessage(message, new AVIMConversationCallback() {
                            @Override
                            public void done(AVIMException e) {
                                if(e!=null)
                                    e.printStackTrace();
                            }
                        });
                    }
                }
            }else{
                toast(error);
            }
        }
    };

    private void setMyPlaneRoute() {
        PlanNode sNode = PlanNode.withLocation(mePin.getPosition());
        PlanNode eNode = PlanNode.withLocation(destinationPin.getPosition());

        WalkingRoutePlanOption option = new WalkingRoutePlanOption();
        option.from(sNode);
        option.to(eNode);
        planSearch.walkingSearch(new WalkingRoutePlanOption());

    }


    private void prepare(AVOGuide guide) {

        if(AVOUser.getCurrentUser().getObjectId().equals(guide.getCreator().getObjectId())){
            finishGuiding.setVisibility(View.VISIBLE);
        }

        LocationService.service().registerCallback(this,locationCallback);

        List<AVOUser> userList = guide.getPartner();
        me = AVOUser.getCurrentUser();
        if(userList.get(0).getObjectId().equals(me.getObjectId())){
            other = userList.get(1);
        }else {
            other = userList.get(0);
        }


        planSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                   toast("抱歉，未找到结果");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    ALog.d(result);
                    result.getSuggestAddrInfo() ;
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    if (result.getRouteLines().size() > 1) {
                        nowResultwalk = result;
                        if (!hasShownDialogue) {
                            MyTransitDlg myTransitDlg = new MyTransitDlg(GuidingActivity.this,
                                    result.getRouteLines(),
                                    RouteLineAdapter.Type.WALKING_ROUTE);
                            myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    hasShownDialogue = false;
                                }
                            });
                            myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                                public void onItemClick(int position) {
                                    nowResultwalk.getRouteLines().get(position);
                                    WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(baiduMap);
                                    baiduMap.setOnMarkerClickListener(overlay);
//                                    routeOverlay = overlay;
                                    overlay.setData(nowResultwalk.getRouteLines().get(position));
                                    overlay.addToMap();
                                    overlay.zoomToSpan();
                                }

                            });
                            myTransitDlg.show();
                            hasShownDialogue = true;
                        }
                        ALog.d(result);
                    } else if (result.getRouteLines().size() == 1) {
                        // 直接显示
                        result.getRouteLines().get(0);
                        WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(baiduMap);
                        baiduMap.setOnMarkerClickListener(overlay);
//                        routeOverlay = overlay;
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        ALog.d(result);
                    } else {
                        toast("结果数<0");
                        return;
                    }
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });



        //绘制目的地的marker
        MapPinNumView dstPinView = new MapPinNumView(this,"目的地",0xffe67e22,14,0xffffffff);
        destinationPin =  MapHelper.drawMarker(baiduMap,new LatLng(guide.getLocation().getLatitude(),
                guide.getLocation().getLongitude()),
                BitmapDescriptorFactory.fromView(dstPinView));
        destinationPin.setDraggable(false);



        //创建临时会话，并通过该会话发送发送位置信息

        App.myApp().getAvimClient().getQuery()
                .whereEqualTo("objectId",guide.getConvId())
                .findInBackground(new AVIMConversationQueryCallback() {
                    @Override
                    public void done(List<AVIMConversation> list, AVIMException e) {
                        if(list==null || list.isEmpty()){
                            toast("无效会话通道");
                        }else {
                            if (e == null) {
                                locConv = list.get(0);
                                ALog.d(locConv);
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
        if(myMessageHandler == null)
            myMessageHandler = new MyMessageHandler();
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class,myMessageHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class,myMessageHandler);
    }


    private class MyMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client) {
            if(locConv !=null && avimConversation.getConversationId().equals(locConv.getConversationId())){
                if(message instanceof AVIMLocationMessage){
                    AVIMLocationMessage locMsg = (AVIMLocationMessage) message;
                    AVGeoPoint point = locMsg.getLocation();

                    if(otherPin == null){
                        //绘制对方的marker
                        MapPinNumView otherView = new MapPinNumView(GuidingActivity.this,"对方",0xffe67e22,14,0xffffffff);
                        otherPin =  MapHelper.drawMarker(baiduMap,new LatLng(other.getLastLocation().getLatitude()
                                        ,other.getLastLocation().getLatitude()),
                                BitmapDescriptorFactory.fromView(otherView));
                        otherPin.setDraggable(false);
                    }

                    otherPin.setPosition(new LatLng(point.getLatitude(),point.getLongitude()));
                }
            }
            super.onMessage(message, avimConversation, client);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationService.service().unregisterCallback(this);
        if(mapView!=null) {
            mapView.onDestroy();
        }
        if(baiduMap !=null) {
            baiduMap.clear();
            baiduMap.removeMarkerClickListener(this);
        }
        baiduMap = null;
        mapView = null;

        planSearch.destroy();


        if(LocationService.service().getLastLocation()!=null){
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
    class MyTransitDlg extends Dialog {

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
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
//                    hasShownDialogue = false;
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
}
